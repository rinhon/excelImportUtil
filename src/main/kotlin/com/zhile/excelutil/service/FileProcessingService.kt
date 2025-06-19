package com.zhile.excelutil.service

import com.zhile.excelutil.dao.*
import com.zhile.excelutil.entity.*
import com.zhile.excelutil.handler.FileProcessingWebSocketHandler
import com.zhile.excelutil.handler.ProgressData
import com.zhile.excelutil.service.ExcelHeaderData.*
import com.zhile.excelutil.utils.FileUtils
import kotlinx.coroutines.*
import org.apache.poi.ss.usermodel.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.fileSize


private val logger = LoggerFactory.getLogger(FileProcessingService::class.java)
private val taskStatus = ConcurrentHashMap<String, TaskStatus>()
private val processingJobs = ConcurrentHashMap<String, Job>()

@Service
class FileProcessingService(
    private val webSocketHandler: FileProcessingWebSocketHandler,
    private val fileUtil: FileUtils,
    private val imDepartmentRepository: ImDepartmentRepository,
    private val imCustomerRepository: ImCustomerRepository,
    private val imUserRepository: ImUserRepository,
    private val imItemRepository: ImItemRepository,
    private val imItemNatureAccountRepository: ImItemNatureAccountRepository,
    private val imPositionRepository: ImPositionRepository,
) {

    /**
     * 取消指定任务
     */
    fun cancelTask(taskId: String): Boolean {
        // 先尝试取消单个任务
        val job = processingJobs[taskId]
        if (job != null) {
            if (job.isActive) {
                job.cancel()
                processingJobs.remove(taskId)
                updateTaskStatus(taskId, "cancelled", 0, "任务已取消")
                return true
            }
        }

        // 如果没有找到单个任务，可能是批量处理中的任务
        // 取消对应的批量任务
        val batchJob = processingJobs.values.find { it.isActive }
        if (batchJob != null) {
            // 标记特定任务为取消
            updateTaskStatus(taskId, "cancelled", 0, "任务已取消")

            // 检查是否所有任务都被取消了，如果是则取消整个批量任务
            val allTasksCancelled =
                taskStatus.values.filter { it.status !in listOf("completed", "error") }.all { it.status == "cancelled" }
            if (allTasksCancelled) {
                batchJob.cancel()
                processingJobs.clear()
            }

            return true
        }

        return false
    }

    /**
     * 取消所有任务
     */
    fun cancelAllTasks(): Boolean {
        var cancelled = false

        // 取消所有正在运行的任务
        processingJobs.values.forEach { job ->
            if (job.isActive) {
                job.cancel()
                cancelled = true
            }
        }

        processingJobs.clear()

        // 更新所有未完成任务的状态
        taskStatus.values.filter { it.status !in listOf("completed", "error", "cancelled") }.forEach { task ->
            updateTaskStatus(task.taskId, "cancelled", 0, "批量任务已取消")
        }

        return cancelled
    }

    /**
     * 清理已完成的任务（可以定期调用）
     */
    fun cleanupCompletedTasks() {
        val cutoffTime = System.currentTimeMillis() - 3600000 // 1小时前
        taskStatus.values.removeIf { task ->
            task.status in listOf("completed", "error", "cancelled") && task.updatedAt < cutoffTime
        }
    }

    /**
     * 获取任务状态
     */
    fun getTaskStatus(taskId: String): TaskStatus? {
        return taskStatus[taskId]
    }

    /**
     * 获取所有任务状态
     */
    fun getAllTasksStatus(taskId: String): TaskStatus? {
        return taskStatus[taskId]
    }

    /**
     * 进程文件异步
     */
    @Transactional
    fun processFilesAsync(files: List<MultipartFile>, tasks: List<Map<String, String>>, sessionId: String) {
        // 每次保存前清空临时目录
        fileUtil.cleanTemporaryStorage()
        //文件持久化存储
        files.forEach { file ->
            try {
                val storedFilePath = fileUtil.saveMultipartFile(file)
                logger.info("文件已持久化到: $storedFilePath")
            } catch (e: Exception) {
                logger.error(e.message)
            }
        }
        val persistenceFiles = fileUtil.listAllStoredFiles()

        // 初始化所有任务状态
        tasks.forEachIndexed { _, task ->
            val taskId = task["taskId"]!!
            val fileName = task["fileName"]!!

            taskStatus[taskId] = TaskStatus(
                taskId = taskId, fileName = fileName, status = "waiting", progress = 0, message = "等待处理"
            )
        }

        // 创建单个协程按顺序处理所有文件
        val job = CoroutineScope(Dispatchers.IO).launch {
            processFilesSequentially(persistenceFiles, tasks, sessionId)
        }

        // 使用批次ID来管理整个处理任务
        val batchId = "batch_${System.currentTimeMillis()}"
        processingJobs[batchId] = job
    }

    /**
     * 按顺序处理excel
     */
    suspend fun processFilesSequentially(
        files: List<Path>, tasks: List<Map<String, String>>, sessionId: String
    ) {
        logger.info("开始依次处理 ${files.size} 个文件")

        try {
            tasks.forEachIndexed { index, task ->
                val taskId = task["taskId"]!!
                val fileName = task["fileName"]!!
                val file = files[index]

                logger.info("开始处理第 ${index + 1}/${files.size} 个文件: $fileName")

                // 更新其他等待中的任务状态
                updateWaitingTasksMessage(sessionId, tasks, index, webSocketHandler)

                // 获取 File 对象
                val dealFile: File = file.toFile()

                // 现在你可以像操作普通 File 对象一样操作它了
                if (dealFile.exists()) {
                    println("文件大小：${file.fileSize()} 字节")
                } else {
                    println("文件不存在：${file}")
                }
                // 处理当前文件
                processFile(taskId, fileName, dealFile, index + 1, files.size, sessionId)

                logger.info("完成处理第 ${index + 1}/${files.size} 个文件: $fileName")

                // 在文件之间添加短暂延迟，避免系统负载过高
                if (index < files.size - 1) {
                    delay(1000)
                }
            }
            logger.info("所有文件处理完成")

        } catch (e: CancellationException) {
            logger.info("批量处理任务已取消", e)
            // 将所有未完成的任务标记为取消
            tasks.forEach { task ->
                val taskId = task["taskId"]!!
                val currentStatus = taskStatus[taskId]
                if (currentStatus?.status !in listOf("completed", "error")) {
                    updateTaskStatus(taskId, "cancelled", 0, "批量任务已取消")
                }
            }
        } catch (e: Exception) {


            logger.error("批量处理过程中发生错误", e)
        }
    }

    /**
     * 更新等待任务消息
     */
    private fun updateWaitingTasksMessage(
        sessionId: String,
        tasks: List<Map<String, String>>,
        currentIndex: Int,
        webSocketHandler: FileProcessingWebSocketHandler
    ) {
        tasks.forEachIndexed { index, task ->
            val taskId = task["taskId"]!!
            val currentStatus = taskStatus[taskId]

            if (index > currentIndex && currentStatus?.status == "waiting") {
                val position = index - currentIndex
                updateTaskStatus(
                    taskId, "waiting", 0, "排队中，前面还有 $position 个文件待处理"
                )

                // 发送等待状态更新
                webSocketHandler.sendToSession(
                    sessionId, ProgressData(
                        type = "processing",
                        taskId = taskId,
                        fileName = task["fileName"]!!,
                        progress = 0,
                        status = "waiting",
                        message = "排队中，前面还有 $position 个文件待处理"
                    )
                )
            }
        }
    }

    /**
     * 开始处理excel文件方法
     */
    suspend fun processFile(
        taskId: String,
        fileName: String,
        file: File,
        currentFileIndex: Int = 1,
        totalFiles: Int = 1,
        sessionId: String,
    ) {
        try {
            // 更新状态为处理中
            updateTaskStatus(taskId, "processing", 5, "开始处理文件 ($currentFileIndex/$totalFiles)")

            // 发送开始处理通知
            webSocketHandler.sendToSession(
                sessionId, ProgressData(
                    type = "processing",
                    taskId = taskId,
                    fileName = fileName,
                    progress = 5,
                    status = "processing",
                    message = "开始处理文件 ($currentFileIndex/$totalFiles)"
                )
            )

            // 将excel数据保存至中间表
            processExcelFile(taskId, fileName, file, currentFileIndex, totalFiles, webSocketHandler, sessionId)


            // 完成处理，通知客户端
            updateTaskStatus(taskId, "completed", 100, "处理完成 ($currentFileIndex/$totalFiles)")

            //再通知客户端调用存储过程中
            //TODO 调用存储过程接口


            // 发送完成通知
            webSocketHandler.sendToSession(
                sessionId, ProgressData(
                    type = "completed",
                    taskId = taskId,
                    fileName = fileName,
                    progress = 100,
                    status = "completed",
                    message = "处理完成 ($currentFileIndex/$totalFiles)"
                )
            )

        } catch (e: CancellationException) {
            updateTaskStatus(taskId, "cancelled", 0, "任务已取消")
            logger.info("任务已取消: $taskId")
            throw e // 重新抛出以停止后续文件处理
        } catch (e: Exception) {
            logger.error("处理文件失败: $fileName", e)
            updateTaskStatus(taskId, "error", 0, "处理失败: ${e.message}")

            // 发送错误通知
            webSocketHandler.sendToSession(
                sessionId, ProgressData(
                    type = "error",
                    taskId = taskId,
                    fileName = fileName,
                    status = "error",
                    message = "处理失败: ${e.message}"
                )
            )
        }
    }


    /**
     * excel处理 为数据库新增数据, 新增完成后
     */
    suspend fun processExcelFile(
        taskId: String,
        fileName: String,
        file: File,
        currentFileIndex: Int,
        totalFiles: Int,
        webSocketHandler: FileProcessingWebSocketHandler,
        sessionId: String
    ) {
        if (!file.exists()) {
            logger.error("未获取到$fileName ($currentFileIndex/$totalFiles)文件流")
            webSocketHandler.sendToSession(
                sessionId, ProgressData(
                    type = "error",
                    taskId = taskId,
                    fileName = fileName,
                    progress = 0,
                    status = "error",
                    message = "后台出错"
                )
            )
            return
        }
        // 检查任务是否被取消
        val currentStatus = taskStatus[taskId]
        if (currentStatus?.status == "cancelled") {
            throw CancellationException("任务已取消")
        }
        try {
            val workbook = WorkbookFactory.create(file.inputStream())
            val sheet = workbook.getSheetAt(0)
            val totalRows = sheet.lastRowNum + 1
            val evaluator = workbook.creationHelper.createFormulaEvaluator()

            logger.info("开始处理Excel文件: $fileName ($currentFileIndex/$totalFiles), 总行数: $totalRows")

            // 检查文件是否为空
            if (totalRows <= 1) {
                updateTaskStatus(taskId, "processing", 50, "文件为空或只有标题行")
                return
            }
            //TODO 开启事务,新增数据
            //获取表头
            // --- 头部数据检测 ---
            // 通常头部在文件的前几行，这里只检查前5行，避免遍历整个文件
            val maxHeaderCheckRows = minOf(totalRows, 5)
            var matchedHeaderInfo: Pair<ExcelHeaderData?, Map<String, Int>>? = null
            var headerRowIndex = -1 // 记录头部所在的行索引
            var headerMatch: Pair<ExcelHeaderData?, Map<String, Int>>?
            for (i in 0 until maxHeaderCheckRows) {
                val row = sheet.getRow(i)
                headerMatch = isValidExcelHeaderRow(row, evaluator)
                if (headerMatch != null) {
                    matchedHeaderInfo = headerMatch
                    headerRowIndex = i
                    logger.info("Excel文件: $fileName 头部在第 ${i + 1} 行找到，匹配类型: ${headerMatch.first?.name}")
                    break // 找到头部后，停止检测
                }
            }

            // 如果没有找到匹配的头部，则认为文件格式不符
            if (matchedHeaderInfo == null) {
                webSocketHandler.sendToSession(
                    sessionId, ProgressData(
                        type = "error",
                        taskId = taskId,
                        fileName = fileName,
                        progress = 100,
                        status = "error", // 状态改为 error
                        message = "表格($fileName)格式不符合要求，未找到匹配的头部"
                    )
                )
                updateTaskStatus(taskId, "error", 100, "表格($fileName)格式不符合要求，未找到匹配的头部")
                return // 终止处理
            }

            val (matchedHeaderType, headerIndexMap) = matchedHeaderInfo
            val dataStartRowIndex = headerRowIndex + 1
            //开始处理数据
            for (i in dataStartRowIndex until totalRows) {
                val row = sheet.getRow(i)
                // 检查行是否为空
                if (row == null) {
                    continue
                }
                when (matchedHeaderType) {
                    // 1.部门 数据中间表
                    DEPARTMENT_HEADERS -> {
                        val imDepartment = ImDepartment()
                        // 使用headerIndexMap来获取正确的列位置
                        headerIndexMap.forEach { (headerName, columnIndex) ->
                            val cellValue = getCellValueAsString(row.getCell(columnIndex), evaluator)
                            when (headerName) {
                                "本级编码", "部门编码" -> imDepartment.code = cellValue
                                "层级", "部门名称" -> imDepartment.name = cellValue
                                "全称" -> imDepartment.fullName = cellValue
                                "父级编码" -> imDepartment.parentCode = cellValue
                                "父级名称" -> imDepartment.parentName = cellValue
                                "行政级别", "部门类型" -> imDepartment.departmentType = cellValue
                                "省份", "备注" -> imDepartment.remarks = cellValue
                            }
                        }
                        imDepartmentRepository.insertDepartment(
                            code = imDepartment.code,
                            name = imDepartment.name,
                            fullName = imDepartment.fullName,
                            parentCode = imDepartment.parentCode,
                            parentName = imDepartment.parentName,
                            departmentType = imDepartment.departmentType,
                            remarks = imDepartment.remarks,
                            id = null,
                            parentId = null,
                            typeId = null
                        )
                    }
                    // 2.往来单位中间表
                    CUSTOMER_HEADERS -> {
                        val imCustomer = ImCustomer()
                        // 使用headerIndexMap来获取正确的列位置
                        headerIndexMap.forEach { (headerName, columnIndex) ->
                            val cellValue = getCellValueAsString(row.getCell(columnIndex), evaluator)
                            when (headerName) {
                                "本级编码", "部门编码" -> imCustomer.code = cellValue
                                "名称", "部门名称" -> imCustomer.name = cellValue
                                "简称" -> imCustomer.abbr = cellValue
                                "分类" -> imCustomer.catalog = cellValue
                                "内部单位" -> imCustomer.inUnit = cellValue
                                "性质1" -> imCustomer.nature1 = cellValue
                                "性质2" -> imCustomer.nature2 = cellValue
                                "类型" -> imCustomer.customerType = cellValue
                                "地区" -> imCustomer.area = cellValue
                                "银行账户" -> imCustomer.bankAccount = cellValue
                                "开户行" -> imCustomer.bank = cellValue
                                "函证联系人" -> imCustomer.correspondenceContact = cellValue
                                "证件类型" -> imCustomer.cardType = cellValue
                                "证件号码" -> imCustomer.cardNo = cellValue
                                "函证人电话" -> imCustomer.correspondenceTel = cellValue
                                "函证人地址" -> imCustomer.correspondenceAddress = cellValue
                            }
                        }
                        imCustomerRepository.insertCustomer(
                            keyId = null,
                            code = imCustomer.code,
                            name = imCustomer.name,
                            abbr = imCustomer.abbr,
                            catalog = imCustomer.catalog,
                            inUnit = imCustomer.inUnit,
                            nature1 = imCustomer.nature1,
                            nature2 = imCustomer.nature2,
                            customerType = imCustomer.customerType,
                            area = imCustomer.area,
                            bankAccount = imCustomer.bankAccount,
                            bank = imCustomer.bank,
                            correspondenceContact = imCustomer.correspondenceContact,
                            cardType = imCustomer.cardType,
                            cardNo = imCustomer.cardNo,
                            correspondenceTel = imCustomer.correspondenceTel,
                            correspondenceAddress = imCustomer.correspondenceAddress,
                            id = null,
                            topBankId = null,
                            areaId = null,
                            customerTypeId = null,
                            cardTypeId = null
                        )


                    }
                    //3.职员数据中间表
                    USER_HEADERS -> {
                        val imUser = ImUser()
                        // 使用headerIndexMap来获取正确的列位置
                        headerIndexMap.forEach { (headerName, columnIndex) ->
                            val cellValue = getCellValueAsString(row.getCell(columnIndex), evaluator)
                            when (headerName) {
                                "职员名称" -> imUser.name = cellValue
                                "职员编码" -> imUser.code = cellValue
                                "手机号码" -> imUser.phone = cellValue
                                "所属部门编码" -> imUser.departmentCode = cellValue
                                "所属部门名称" -> imUser.departmentName = cellValue
                                "性别" -> imUser.sex = cellValue
                                "备注" -> imUser.remarks = cellValue
                            }
                        }
                        imUserRepository.insertUser(
                            keyId = null,
                            code = imUser.code,
                            name = imUser.name,
                            phone = imUser.phone,
                            departmentCode = imUser.departmentCode,
                            departmentName = imUser.departmentName,
                            sex = imUser.sex,
                            remarks = imUser.remarks,
                            id = null,
                            departmentId = null
                        )
                    }
                    //4.货位中间表
                    POSITION_HEADERS -> {
                        val imPosition = ImPosition()
                        // 使用headerIndexMap来获取正确的列位置
                        headerIndexMap.forEach { (headerName, columnIndex) ->
                            val cellValue = getCellValueAsString(row.getCell(columnIndex), evaluator)
                            when (headerName) {
                                "本级编码", "编码" -> imPosition.code = cellValue
                                "名称" -> imPosition.name = cellValue
                                "备注" -> imPosition.remarks = cellValue
                                "运营方" -> imPosition.manager = cellValue
                                "地址" -> imPosition.address = cellValue
                            }
                            imPositionRepository.save(imPosition)
                        }
                    }
                    //5.物品中间表
                    ITEM_HEADERS -> {
                        val imItem = ImItem()
                        // 使用headerIndexMap来获取正确的列位置
                        headerIndexMap.forEach { (headerName, columnIndex) ->
                            val cellValue = getCellValueAsString(row.getCell(columnIndex), evaluator)
                            when (headerName) {
                                "物品编码" -> imItem.code = cellValue
                                "物品名称" -> imItem.name = cellValue
                                "物品简称" -> imItem.abbr = cellValue
                                "条码" -> imItem.barCode = cellValue
                                "定价" -> imItem.setPrice = cellValue
                                "规格型号" -> imItem.spec = cellValue
                                "出版类别" -> imItem.publishType = cellValue
                                "经营方式" -> imItem.publishMethod = cellValue
                                "物品分类" -> imItem.category = cellValue
                                "物品类型" -> imItem.itemType = cellValue
                                "财务分类" -> imItem.nature = cellValue
                                "长度" -> imItem.length = cellValue
                                "宽度" -> imItem.width = cellValue
                                "高度" -> imItem.height = cellValue
                                "规格包装" -> imItem.packUnit = cellValue
                                "计量单位" -> imItem.unit = cellValue
                                "是否套装物品" -> imItem.kit = cellValue
                                "ISBN" -> imItem.isbn = cellValue
                                "附加码" -> imItem.auxCode = cellValue
                                "丛书名" -> imItem.seriesName = cellValue
                                "副书名" -> imItem.viceBookName = cellValue
                                "版次时间-年月" -> imItem.editionYearMonth = cellValue
                                "版次序号" -> imItem.editionNo = cellValue
                                "主要作者" -> imItem.mainAuthor = cellValue
                                "编辑部门编码" -> imItem.departmentCode = cellValue
                                "编辑部门名称" -> imItem.departmentName = cellValue
                                "责任编辑" -> imItem.dutyEditorName = cellValue
                                "责任编辑编码" -> imItem.dutyEditorCode = cellValue
                                "出版期间" -> imItem.publishPeriod = cellValue
                                "印张" -> imItem.printSheet = cellValue
                                "开本" -> imItem.format = cellValue
                                "开本尺寸" -> imItem.formatSize = cellValue
                                "选题类别" -> imItem.topicType = cellValue
                                "装订方式" -> imItem.bindingType = cellValue
                                "文种" -> imItem.language = cellValue
                                //                            "正文文字" -> imItem.bodyText = cellValue
                                "内容简介" -> imItem.summary = cellValue
                                "前言" -> imItem.perface = cellValue
                                "目录" -> imItem.catalog = cellValue
                                "书评" -> imItem.bookReview = cellValue
                                "摘要" -> imItem.bookAbstract = cellValue
                                "CIP信息" -> imItem.cipInfo = cellValue
                                "备注" -> imItem.remarks = cellValue
                            }
                            imItemRepository.insertItem(
                                keyId = null,
                                code = imItem.code,
                                name = imItem.name,
                                abbr = imItem.abbr,
                                barCode = imItem.barCode,
                                setPrice = imItem.setPrice,
                                spec = imItem.spec,
                                publishType = imItem.publishType,
                                publishMethod = imItem.publishMethod,
                                category = imItem.category,
                                itemType = imItem.itemType,
                                nature = imItem.nature,
                                length = imItem.length,
                                width = imItem.width,
                                height = imItem.height,
                                packUnit = imItem.packUnit,
                                unit = imItem.unit,
                                kit = imItem.kit,
                                isbn = imItem.isbn,
                                auxCode = imItem.auxCode,
                                seriesName = imItem.seriesName,
                                viceBookName = imItem.viceBookName,
                                editionYearMonth = imItem.editionYearMonth,
                                editionNo = imItem.editionNo,
                                mainAuthor = imItem.mainAuthor,
                                departmentCode = imItem.departmentCode,
                                departmentName = imItem.departmentName,
                                dutyEditorCode = imItem.departmentCode,
                                dutyEditorName = imItem.dutyEditorName,
                                publishPeriod = imItem.publishPeriod,
                                printSheet = imItem.printSheet,
                                format = imItem.format,
                                formatSize = imItem.formatSize,
                                topicType = imItem.topicType,
                                bindingType = imItem.bindingType,
                                language = imItem.language,
                                noteLanguage = imItem.noteLanguage,
                                summary = imItem.summary,
                                perface = imItem.perface,
                                catalog = imItem.catalog,
                                bookReview = imItem.bookReview,
                                bookAbstract = imItem.bookAbstract,
                                cipInfo = imItem.cipInfo,
                                remarks = imItem.remarks,
                                id = null,
                                itemTypeId = null,
                                editDepartmentId = null,
                                dutyEditorId = null,
                                publishPeriodId = null,
                                formatId = null,
                                formatSizeId = null,
                                natureId = null,
                                languageId = null,
                                noteLanguageId = null,
                                publishMethodId = null,
                                unitId = null,
                                bindingTypeId = null
                            )

                        }

                    }
                    //6.物品性质与科目中间表
                    ITEM_NATURE_ACCOUNT_HEADERS -> {
                        val imItemNatureAccount = ImItemNatureAccount()
                        // 使用headerIndexMap来获取正确的列位置
                        headerIndexMap.forEach { (headerName, columnIndex) ->
                            val cellValue = getCellValueAsString(row.getCell(columnIndex), evaluator)
                            when (headerName) {
                                "物品性质" -> imItemNatureAccount.inventoryAcctCode = cellValue
                                "存货科目" -> imItemNatureAccount.inventoryAcctCode = cellValue
                                "主营业务收入科目" -> imItemNatureAccount.incomeAcctCode = cellValue
                                "主营业务成本科目" -> imItemNatureAccount.costAcctCode = cellValue
                                "进项税科目" -> imItemNatureAccount.inputTaxAcctCode = cellValue
                                "销项税科目" -> imItemNatureAccount.outputTaxAcctCode = cellValue
                                "发出商品科目" -> imItemNatureAccount.stockOutItemAcctCode = cellValue
                                "暂估应付科目" -> imItemNatureAccount.prolEsteAcctCode = cellValue
                                "暂估应收款科目" -> imItemNatureAccount.prolEsteReceiveAcctCode = cellValue
                                "暂估进项科目" -> imItemNatureAccount.prolEsteInputAcctCode = cellValue
                                "暂估销项科目" -> imItemNatureAccount.prolEsteOutputAcctCode = cellValue
                            }
                            imItemNatureAccountRepository.insertItemNatureAccount(
                                keyId = null,
                                itemNature = imItemNatureAccount.itemNature,
                                inventoryAcctCode = imItemNatureAccount.inventoryAcctCode,
                                incomeAcctCode = imItemNatureAccount.incomeAcctCode,
                                costAcctCode = imItemNatureAccount.costAcctCode,
                                inputTaxAcctCode = imItemNatureAccount.inputTaxAcctCode,
                                outputTaxAcctCode = imItemNatureAccount.outputTaxAcctCode,
                                stockOutItemAcctCode = imItemNatureAccount.stockOutItemAcctCode,
                                prolEsteAcctCode = imItemNatureAccount.prolEsteAcctCode,
                                prolEsteReceiveAcctCode = imItemNatureAccount.prolEsteReceiveAcctCode,
                                prolEsteInputAcctCode = imItemNatureAccount.prolEsteInputAcctCode,
                                prolEsteOutputAcctCode = imItemNatureAccount.prolEsteOutputAcctCode,
                                itemNatureId = null
                            )
                        }

                    }

                    null -> {
                        // 未匹配到任何类型，跳过
                        continue
                    }
                }
                // 进度计算：10% - 90%
                val progress = 10 + ((i + 1) * 80 / totalRows)
                val message = "正在处理 ($currentFileIndex/$totalFiles) - 第 ${i + 1}/$totalRows 行"

                updateTaskStatus(taskId, "processing", progress, message)

                // 每处理5行或最后一行时发送进度更新
                if ((i + 1) % 5 == 0 || i == totalRows - 1) {
                    webSocketHandler.sendToSession(
                        sessionId, ProgressData(
                            type = "processing",
                            taskId = taskId,
                            fileName = fileName,
                            progress = progress,
                            status = "processing",
                            message = message
                        )
                    )
                }
            }
            //关闭
            workbook.close()
        } catch (e: NoSuchFileException) {
            logger.error(e.message)
            logger.error(e.reason)
            println(e.message)
            println(e.reason)
            webSocketHandler.sendToSession(
                sessionId, ProgressData(
                    type = "error",
                    taskId = taskId,
                    fileName = fileName,
                    progress = 95,
                    status = "error",
                    message = "后台出错"
                )
            )
        }

        // 最后的数据保存
        val saveMessage = "表格数据导入完成, 请等待最终导入...(数据量越大等待时间越长,请勿关闭窗口)"
        updateTaskStatus(taskId, "processing", 95, saveMessage)

        webSocketHandler.sendToSession(
            sessionId, ProgressData(
                type = "processing",
                taskId = taskId,
                fileName = fileName,
                progress = 95,
                status = "processing",
                message = saveMessage
            )
        )
    }

    /**
     * 安全地获取单元格中的数据，并将其统一转换为 String 类型。
     * 数字在转换为字符串时，如果为整数（如 123.0），将转换为不带小数点的形式（如 "123"）。
     *
     * @param cell 要获取数据的单元格对象，可能为 null。
     * @param evaluator FormulaEvaluator 实例，用于评估公式单元格。
     * @return 单元格数据的 String 表示。如果单元格为 null 或空白，返回空字符串。
     */
    fun getCellValueAsString(cell: Cell?, evaluator: FormulaEvaluator): String {
        if (cell == null) {
            return "" // 如果单元格本身是 null，返回空字符串
        }

        return when (cell.cellType) {
            CellType.STRING -> cell.stringCellValue.trim() // 字符串类型
            CellType.NUMERIC -> {
                // 数字类型可能表示数字或日期
                if (DateUtil.isCellDateFormatted(cell)) {
                    // 如果是日期格式的数字，获取日期值
                    cell.dateCellValue.toString() // 或者格式化为特定日期字符串
                } else {
                    // 普通数字
                    val numericValue = cell.numericCellValue
                    // 判断是否为整数，并进行转换
                    if (numericValue % 1.0 == 0.0) { // 如果数字的浮点部分为 0
                        numericValue.toLong().toString() // 转换为 Long 再转 String
                    } else {
                        numericValue.toString() // 否则保持浮点数形式
                    }
                }
            }

            CellType.BOOLEAN -> cell.booleanCellValue.toString() // 布尔类型
            CellType.FORMULA -> {
                try {
                    val cellValue = evaluator.evaluate(cell)
                    when (cellValue.cellType) {
                        CellType.STRING -> cellValue.stringValue.trim()
                        CellType.NUMERIC -> {
                            if (DateUtil.isCellDateFormatted(cell)) { // 公式结果也可能是日期
                                DateUtil.getJavaDate(cellValue.numberValue).toString()
                            } else {
                                val numericValue = cellValue.numberValue
                                // 判断是否为整数，并进行转换
                                if (numericValue % 1.0 == 0.0) { // 如果数字的浮点部分为 0
                                    numericValue.toLong().toString() // 转换为 Long 再转 String
                                } else {
                                    numericValue.toString() // 否则保持浮点数形式
                                }
                            }
                        }

                        CellType.BOOLEAN -> cellValue.booleanValue.toString()
                        CellType.ERROR -> "#ERROR!" // 公式错误
                        else -> "" // 其他未知类型
                    }
                } catch (e: Exception) {
                    // 捕获公式评估中的任何异常，例如公式无效
                    logger.error(e.message)
                    println("警告: 评估公式时出错 ${cell.address.formatAsString()}: ${e.message}")
                    "#FORMULA_ERROR!"
                }
            }

            CellType.BLANK -> "" // 空白单元格
            CellType.ERROR -> "#ERROR!" // 错误单元格
            else -> "" // 其他未知类型
        }
    }


    /**
     * 更新任务状态
     */
    private fun updateTaskStatus(taskId: String, status: String, progress: Int, message: String) {
        taskStatus[taskId] = taskStatus[taskId]?.copy(
            status = status, progress = progress, message = message, updatedAt = System.currentTimeMillis()
        ) ?: TaskStatus(
            taskId = taskId, fileName = "", status = status, progress = progress, message = message
        )
    }

    /**
     * 检查给定的 Excel 行是否包含 ExcelHeaderData 枚举中定义的必要属性。
     * 不要求完全匹配或顺序一致，支持模糊匹配（忽略大小写、空格、常见标点符号）。
     *
     * @param row 从 Excel 表格中获取的当前行对象。
     * @param evaluator 用于评估公式的 FormulaEvaluator 实例。
     * @return 如果行包含任何一个预定义头部列表的所有必要属性，则返回对应的ExcelHeaderData和列索引映射；否则返回null。
     */
    fun isValidExcelHeaderRow(row: Row, evaluator: FormulaEvaluator): Pair<ExcelHeaderData?, Map<String, Int>>? {
        val rowData = mutableListOf<String>()
        val headerIndexMap = mutableMapOf<String, Int>() // 存储标准列名与索引的映射
        val normalizedHeaderMap = mutableMapOf<String, String>() // 存储标准列名与实际列名的映射

        // 获取行中的所有列名
        for (i in 0 until row.lastCellNum) {
            val cell = row.getCell(i)
            val cellValue = if (cell != null) {
                when (cell.cellType) {
                    CellType.STRING -> cell.stringCellValue.trim()
                    CellType.NUMERIC -> cell.numericCellValue.toString()
                    CellType.BOOLEAN -> cell.booleanCellValue.toString()
                    CellType.FORMULA -> {
                        val cellValue = evaluator.evaluate(cell)
                        when (cellValue.cellType) {
                            CellType.STRING -> cellValue.stringValue.trim()
                            CellType.NUMERIC -> cellValue.numberValue.toString()
                            CellType.BOOLEAN -> cellValue.booleanValue.toString()
                            else -> ""
                        }
                    }

                    else -> ""
                }
            } else ""

            if (cellValue.isNotEmpty()) {
                rowData.add(cellValue)
                // 存储原始列名和索引
                headerIndexMap[cellValue] = i
            }
        }

        // 检查每个预定义的表头格式
        for (headerEnum in ExcelHeaderData.entries) {
            // 创建一个映射来存储匹配的标准列名和实际列名
            val matchedHeaders = mutableMapOf<String, String>()

            // 检查是否包含所有必要的属性（使用模糊匹配）
            val containsAllRequired = headerEnum.headers.all { required ->
                val match = rowData.find { actual ->
                    // 标准化字符串进行比较（忽略大小写、空格、常见标点符号）
                    normalizeString(actual).contains(normalizeString(required)) || normalizeString(required).contains(
                        normalizeString(actual)
                    )
                }
                if (match != null) {
                    matchedHeaders[required] = match
                    normalizedHeaderMap[required] = match
                }
                match != null
            }

            if (containsAllRequired) {
                logger.info("找到匹配的表头格式: ${headerEnum.name}")
                // 创建新的headerIndexMap，使用标准列名作为键
                val standardizedHeaderIndexMap = matchedHeaders.mapValues { (_, actualHeader) ->
                    headerIndexMap[actualHeader] ?: -1
                }
                return Pair(headerEnum, standardizedHeaderIndexMap)
            }
        }

        logger.info("未找到匹配的表头格式")
        return null
    }

    /**
     * 标准化字符串以进行比较
     * 1. 转换为小写
     * 2. 移除所有空格
     * 3. 移除常见标点符号
     * 4. 统一全角字符为半角字符
     */
    private fun normalizeString(input: String): String {
        return input.lowercase().replace(Regex("\\s+"), "") // 移除所有空格
            .replace(Regex("[,.:;，。：；、]"), "") // 移除常见标点符号
            .replace("（", "(").replace("）", ")").replace("【", "[").replace("】", "]").trim()
    }


}

/**
 * @author Rinhon
 * @date 2025/6/17 09:05
 * @description: 表头枚举类
 */
enum class ExcelHeaderData(val headers: List<String>) {
    DEPARTMENT_HEADERS(
        listOf(
            "部门编码", "部门名称", "全称", "父级编码", "父级名称", "部门类型", "备注"
        )
    ),
    USER_HEADERS(
        listOf(
            "职员名称", "职员编码", "手机号码", "所属部门编码", "所属部门名称", "性别", "备注"
        )
    ),
    POSITION_HEADERS(
        listOf(
            "编码", "名称", "备注", "地址", "管理员"
        )
    ),
    CUSTOMER_HEADERS(
        listOf(
            "编码",
            "名称",
            "简称",
            "分类",
            "内部单位",
            "性质1",
            "性质2",
            "类型",
            "地区",
            "银行账户",
            "开户行",
            "函证联系人",
            "证件类型",
            "证件号码",
            "函证人电话",
            "函证人地址"
        )
    ),
    ITEM_HEADERS(
        listOf(
            "物品编码",
            "物品名称",
            "物品简称",
            "条形码",
            "定价",
            "规格型号",
            "出版类别",
            "经营方式",
            "物品分类",
            "物品类型",
            "财务分类",
            "长度",
            "宽度",
            "高度",
            "规格包装",
            "计量单位",
            "是否套装物品",
            "ISBN",
            "附加码",
            "丛书名",
            "副书名",
            "版次时间-年月",
            "版次序号",
            "主要作者",
            "编辑部门编码",
            "编辑部门名称",
            "责任编辑名称",
            "责任编辑编码",
            "出版期间",
            "印张",
            "开本",
            "开本尺寸",
            "选题类别",
            "装订方式",
            "正文文字",
            "文种",
            "内容简介",
            "前言",
            "目录",
            "书评",
            "摘要",
            "CIP信息",
            "备注"
        )
    ),
    ITEM_NATURE_ACCOUNT_HEADERS(
        listOf(
            "物品性质",
            "存货科目",
            "主营业务收入科目",
            "主营业务成本科目",
            "进项税科目",
            "销项税科目",
            "发出商品科目",
            "暂估应付科目",
            "暂估应收款科目",
            "暂估进项科目",
            "暂估销项科目"
        )
    )
}

/**
 * @author Rinhon
 * @date 2025/6/17 09:08
 * @description: 任务数据类
 */
data class TaskStatus(
    val taskId: String,
    val fileName: String,
    val status: String, // "waiting", "processing", "completed", "error", "cancelled"
    val progress: Int,
    val message: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
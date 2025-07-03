package com.zhile.excelutil.service

import com.zhile.excelutil.dao.*
import com.zhile.excelutil.entity.*
import com.zhile.excelutil.handler.FileProcessingWebSocketHandler
import com.zhile.excelutil.handler.ProgressData
import com.zhile.excelutil.service.ExcelHeaderData.*
import com.zhile.excelutil.utils.ExcelDealUtils
import com.zhile.excelutil.utils.FileUtils
import kotlinx.coroutines.*
import org.apache.poi.ss.usermodel.FormulaEvaluator
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.fileSize


//日志
private val logger = LoggerFactory.getLogger(FileProcessingService::class.java)

//任务
private val taskStatus = ConcurrentHashMap<String, TaskStatus>()

//任务队列
private val processingJobs = ConcurrentHashMap<String, Job>()

@Service
class FileProcessingService(
    private val oraclePackageService: OraclePackageService,
    private val webSocketHandler: FileProcessingWebSocketHandler,
    private val fileUtil: FileUtils,
    private val imDepartmentRepository: ImDepartmentRepository,
    private val imCustomerRepository: ImCustomerRepository,
    private val imUserRepository: ImUserRepository,
    private val imItemRepository: ImItemRepository,
    private val imItemNatureAccountRepository: ImItemNatureAccountRepository,
    private val imPositionRepository: ImPositionRepository,
    private val imSellBillRepository: ImSellBillRepository,
    private val imDepartmentTypeRepository: ImDepartmentTypeRepository,
    private val imRoleRepository: ImRoleRepository,
    private val imCostItemAccountRepository: ImCostItemAccountRepository,
    private val imStockInitRepository: ImStockInitRepository,
    private val imImportResultRepository: ImImportResultRepository,
    private val imUserRoleRepository: ImUserRoleRepository,
    private val imPositionUserRepository: ImPositionUserRepository,
    private val excelDealUtils: ExcelDealUtils,
    private val imCustomerBusinessSetRepository: ImCustomerBusinessSetRepository,
    private val imSellInvoiceRepository: ImSellInvoiceRepository,
    private val imSellReserveRepository: ImSellReserveRepository,
    private val imPurchaseInvoiceRepository: ImPurchaseInvoiceRepository,
    private val imTopicRecordRepository: ImTopicRecordRepository,
    private val imTopicRecordAuthorRepository: ImTopicRecordAuthorRepository,
    private val imFeeBillRepository: ImFeeBillRepository

) {

    /**
     * 进程文件异步
     */
    @Transactional(rollbackForClassName = ["ImportDataException", "Exception"])
    fun processFilesAsync(
        files: List<MultipartFile>,
        tasks: List<Map<String, String>>,
        sessionId: String,
        organId: Long
    ) {
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
            processFilesSequentially(persistenceFiles, tasks, sessionId, organId)
        }

        // 使用批次ID来管理整个处理任务
        val batchId = "batch_${System.currentTimeMillis()}"
        processingJobs[batchId] = job
    }

    /**
     * 按顺序处理excel
     */
    suspend fun processFilesSequentially(
        files: List<Path>, tasks: List<Map<String, String>>, sessionId: String, organId: Long
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

                if (dealFile.exists()) {
                    println("文件大小：${file.fileSize()} 字节")
                } else {
                    println("文件不存在：${file}")
                }
                // 处理当前文件
                processFile(taskId, fileName, dealFile, index + 1, files.size, sessionId, organId)

                logger.info("完成处理第 ${index + 1}/${files.size} 个文件: $fileName")

                // 在文件之间添加短暂延迟，避免系统负载过高
                if (index < files.size - 1) {
                    delay(1000)
                }
            }
            // 检查每个任务的状态，只对成功处理但未发送完成通知的任务发送通知
            tasks.forEachIndexed { _, task ->
                val taskId = task["taskId"]!!
                val fileName = task["fileName"]!!
                val currentStatus = taskStatus[taskId]

                // 只有当任务状态为processing且进度不为100%时才发送完成通知
                // 这样可以避免对已经发送过完成或错误通知的任务重复发送
                if (currentStatus?.status == "processing" && currentStatus.progress < 100) {
                    val completeMessage = "文件处理完成"
                    updateTaskStatus(taskId, "completed", 100, completeMessage)
                    webSocketHandler.sendToSession(
                        sessionId, ProgressData(
                            type = "completed",
                            taskId = taskId,
                            fileName = fileName,
                            progress = 100,
                            status = "completed",
                            message = completeMessage
                        )
                    )
                    logger.info("发送完成通知: $fileName (taskId: $taskId)")
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
        organId: Long
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

            processExcelFile(
                taskId,
                fileName,
                file,
                currentFileIndex,
                totalFiles,
                webSocketHandler,
                sessionId,
                organId,
            )


            // 完成处理，通知客户端
            updateTaskStatus(taskId, "completed", 100, "处理完成 ($currentFileIndex/$totalFiles)")


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
        sessionId: String,
        organId: Long,
    ): Boolean {
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
            return false
        }
        // 检查任务是否被取消
        val currentStatus = taskStatus[taskId]
        if (currentStatus?.status == "cancelled") {
            throw CancellationException("任务已取消")
        }
        try {
            val workbook = XSSFWorkbook(file.inputStream())

            // 标记循环
            skipSheetHeaderCheck@ for (i in 0 until workbook.numberOfSheets) {
                val sheet = workbook.getSheetAt(i)

                println("开始处理工作簿:${fileName}------------ ${sheet.sheetName}")
                // 跳过名为以下字段的工作簿
                when (sheet.sheetName) {
                    "填写说明" -> continue@skipSheetHeaderCheck
//                    "部门类型" -> continue@skipSheetHeaderCheck
                    "字典表" -> continue@skipSheetHeaderCheck
                    "字典" -> continue@skipSheetHeaderCheck
                    "地图社审批参考" -> continue@skipSheetHeaderCheck
                    "岭南社审批参考" -> continue@skipSheetHeaderCheck
                    "组织架构图" -> continue@skipSheetHeaderCheck
                    "其他数据" -> continue@skipSheetHeaderCheck
                    "其它数据" -> continue@skipSheetHeaderCheck
                    "其他" -> continue@skipSheetHeaderCheck
                    "其它" -> continue@skipSheetHeaderCheck
//                    "ERP物品类型&财务分类&分线产品对应表" -> continue@skipSheetHeaderCheck
                }
                val totalRows = sheet.lastRowNum + 1
                val evaluator = workbook.creationHelper.createFormulaEvaluator()

                logger.info("开始处理Excel文件: {{$fileName}}{{ ${sheet.sheetName}}}($currentFileIndex/$totalFiles) , 总行数: $totalRows")

                // 检查文件是否为空
                if (totalRows <= 1) {
                    updateTaskStatus(taskId, "processing", 50, "工作簿为空或只有标题行")
                    return false
                }
                //获取表头
                // --- 头部数据检测 ---
                // 只检查前5行，避免遍历整个文件
                val maxHeaderCheckRows = minOf(totalRows, 5)
                var matchedHeaderInfo: Pair<ExcelHeaderData?, Map<String, Int>>? = null
                var headerRowIndex = -1 // 记录头部所在的行索引
                var headerMatch: Pair<ExcelHeaderData?, Map<String, Int>>?
                findSheetHeader@ for (j in 0 until maxHeaderCheckRows) {
                    val row = sheet.getRow(j)
                    headerMatch = excelDealUtils.isValidExcelHeaderRow(row, evaluator)
                    if (headerMatch != null) {
                        matchedHeaderInfo = headerMatch
                        headerRowIndex = j
                        logger.info("Excel文件:{{ $fileName}} 头部在第 {{${j + 1}}}行找到，匹配类型:{{${headerMatch.first?.name}}}")
                        break@findSheetHeader // 找到头部后，停止检测
                    }
                }
                // 如果没有找到匹配的头部，则认为文件格式不符
                if (matchedHeaderInfo == null) {
//                    webSocketHandler.sendToSession(
//                        sessionId, ProgressData(
//                            type = "error",
//                            taskId = taskId,
//                            fileName = fileName,
//                            progress = 100,
//                            status = "error", // 状态改为 error
//                            message = "表格 $fileName $number 工作簿,格式不符合要求，未找到匹配的头部"
//                        )
//                    )
//                    updateTaskStatus(taskId, "error", 100, "表格($fileName)格式不符合要求，未找到匹配的头部")
                    println("跳过该工作簿")
                    continue@skipSheetHeaderCheck// 终止处理
                }

                val (matchedHeaderType, headerIndexMap) = matchedHeaderInfo
                val dataStartRowIndex = headerRowIndex + 1
                //根据表头先清空对应的中间表
                when (matchedHeaderType) {
                    DEPARTMENT_HEADERS -> imDepartmentRepository.deleteAllImDepartment()
                    DEPARTMENT_TYPE_HEADERS -> imDepartmentTypeRepository.deleteAllDepartmentType()
                    USER_HEADERS -> imUserRepository.deleteAllImUser()
                    ROLE_HEADERS -> imRoleRepository.deleteAllImRole()
                    USER_ROLE_HEADERS -> imUserRoleRepository.deleteAllImUserRole()
                    POSITION_HEADERS -> imPositionRepository.deleteAllImPosition()
                    IM_POSITION_USERS_HEADERS -> imPositionUserRepository.deleteAllImPositionUser()
                    CUSTOMER_HEADERS -> imCustomerRepository.deleteAllImCustomer()
                    ITEM_HEADERS -> imItemRepository.deleteAllImItem()
                    ITEM_NATURE_ACCOUNT_HEADERS -> imItemNatureAccountRepository.deleteAllImItemNatureAccount()
                    IM_COST_ITEM_ACCOUNT_HEADERS -> imCostItemAccountRepository.deleteAllImCostItemAccount()
                    IM_STOCK_INIT_HEADERS -> imStockInitRepository.deleteAllImStockInit()
                    SELL_BILL_HEADERS -> imSellBillRepository.deleteAllImSellBill()
                    CUSTOMER_BUSINESS_SET_HEADERS -> imCustomerBusinessSetRepository.deleteAllImCustomerBusinessSet()
                    SELL_INVOICE_HEADERS -> imSellInvoiceRepository.deleteAllImSellInvoice()
                    SELL_RESERVE_HEADERS -> imSellReserveRepository.deleteAllImSellReserve()
                    PURCHASE_INVOICE_HEADERS -> imPurchaseInvoiceRepository.deleteAllPurchaseInvoice()
                    TOPIC_RECORD_HEADERS -> imTopicRecordRepository.deleteAllImTopicRecord()
                    TOPIC_RECORD_AUTHOR_HEADERS -> imTopicRecordAuthorRepository.deleteAllTopicRecordAuthor()
                    FEE_BILL_HEADERS -> imFeeBillRepository.deleteAllFeeBill()
                    OTHERS_SKIP_HEADERS -> continue@skipSheetHeaderCheck
                    null -> continue@skipSheetHeaderCheck
                }
                //开始处理数据
                dealData@ for (j in dataStartRowIndex until totalRows) {
                    val row = sheet.getRow(j)
                    // 检查行是否为空
                    if (row == null) {
                        continue@dealData
                    }
                    // 在处理每一行之前，添加检查行是否包含有效数据的逻辑
                    var hasData = false
                    checkCell@ for (x in 0 until row.lastCellNum) {
                        val cellValue = excelDealUtils.getCellValueAsString(row.getCell(x), evaluator)
                        if (cellValue.isNotEmpty()) {
                            hasData = true
                            break@checkCell
                        }
                    }
                    if (!hasData) {
                        continue@dealData // 跳过没有任何数据的行
                    }
                    try {
                        // 记录开始处理的行号
                        logger.debug("开始处理第 ${j + 1} 行数据")
                        when (matchedHeaderType) {
                            // 1.1部门 类型中间表-->部门设置表-工作簿2  XXXX字段对不上
                            DEPARTMENT_TYPE_HEADERS -> {
                                val imDepartmentType = ImDepartmentType()
                                // 使用headerIndexMap来获取正确的列位置
                                headerIndexMap.forEach { (headerName, columnIndex) ->
                                    val cellValue =
                                        excelDealUtils.getCellValueAsString(row.getCell(columnIndex), evaluator)
                                    when (headerName) {
                                        "顺序号" -> imDepartmentType.seq = cellValue
                                        "部门类型" -> imDepartmentType.name = cellValue
                                        "备注" -> imDepartmentType.remarks = cellValue
                                    }
                                }
                                imDepartmentTypeRepository.insertDepartmentType(
                                    seq = imDepartmentType.seq,
                                    name = imDepartmentType.name,
                                    remarks = imDepartmentType.remarks,
                                    id = null,
                                    code = null
                                )
                            }
                            // 1.2部门 数据中间表-->部门设置表-工作簿1
                            DEPARTMENT_HEADERS -> {
                                val imDepartment = ImDepartment()
                                // 使用headerIndexMap来获取正确的列位置
                                headerIndexMap.forEach { (headerName, columnIndex) ->
                                    val cellValue =
                                        excelDealUtils.getCellValueAsString(row.getCell(columnIndex), evaluator)
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
                                    parentId = null,
                                    typeId = null
                                )
                            }
                            // 2.职员数据中间表-->职员设置表-工作簿1  √√√√√√ 缺少 用户属性 查询权限
                            USER_HEADERS -> {
                                val imUser = ImUser()
                                // 使用headerIndexMap来获取正确的列位置
                                headerIndexMap.forEach { (headerName, columnIndex) ->
                                    val cellValue =
                                        excelDealUtils.getCellValueAsString(row.getCell(columnIndex), evaluator)
                                    when (headerName) {
                                        "职员名称" -> imUser.name = cellValue
                                        "职员编码" -> imUser.code = cellValue
                                        "手机号码" -> imUser.phone = cellValue
                                        "所属部门编码" -> imUser.departmentCode = cellValue
                                        "所属部门名称" -> imUser.departmentName = cellValue
                                        "性别" -> imUser.sex = cellValue
                                        "能否登录系统" -> imUser.login = cellValue
                                        "备注" -> imUser.remarks = cellValue
                                        "用户属性" -> imUser.attribute = cellValue
                                        "查询权限" -> imUser.queryRight = cellValue
                                    }
                                }
                                imUserRepository.insertUser(
                                    code = imUser.code,
                                    name = imUser.name,
                                    phone = imUser.phone,
                                    departmentCode = imUser.departmentCode,
                                    departmentName = imUser.departmentName,
                                    sex = imUser.sex,
                                    remarks = imUser.remarks,
                                    login = imUser.login,
                                    attribute = imUser.attribute,
                                    queryRight = imUser.queryRight,
                                    id = null,
                                    departmentId = null
                                )
                            }
                            //3.1 角色数据中间表-->职员角色设置表-工作簿2  √√√√√√
                            ROLE_HEADERS -> {
                                val imUser = ImRole()
                                // 使用headerIndexMap来获取正确的列位置
                                headerIndexMap.forEach { (headerName, columnIndex) ->
                                    val cellValue =
                                        excelDealUtils.getCellValueAsString(row.getCell(columnIndex), evaluator)
                                    when (headerName) {
                                        "角色名称" -> imUser.name = cellValue
                                        "角色编码" -> imUser.code = cellValue
                                    }
                                }
                                imRoleRepository.insertRole(
                                    code = imUser.code,
                                    name = imUser.name,
                                    id = null,
                                )
                            }
                            // 3.2 职员角色中间表-->职员角色设置表-工作簿1  √√√√√√
                            USER_ROLE_HEADERS -> {
                                val imUserRole = ImUserRole()
                                headerIndexMap.forEach { (headerName, columnIndex) ->
                                    val cellValue =
                                        excelDealUtils.getCellValueAsString(row.getCell(columnIndex), evaluator)
                                    when (headerName) {
                                        "职员编码" -> imUserRole.userCode = cellValue
                                        "职员名称" -> imUserRole.userName = cellValue
                                        "角色编码" -> imUserRole.roleCode = cellValue
                                        "角色名称" -> imUserRole.roleName = cellValue
                                    }
                                }
                                imUserRoleRepository.insertUserRole(
                                    userCode = imUserRole.userCode,
                                    userName = imUserRole.userName,
                                    roleCode = imUserRole.roleCode,
                                    roleName = imUserRole.roleName,
                                    userId = null,
                                    roleId = null
                                )
                            }
                            // 4.货位中间表-->业务员、仓库人员分配-工作簿1  √√√√√√
                            POSITION_HEADERS -> {
                                val imPosition = ImPosition()
                                // 使用headerIndexMap来获取正确的列位置
                                headerIndexMap.forEach { (headerName, columnIndex) ->
                                    val cellValue =
                                        excelDealUtils.getCellValueAsString(row.getCell(columnIndex), evaluator)
                                    when (headerName) {
                                        "仓库编码" -> imPosition.code = cellValue
                                        "仓库名称" -> imPosition.name = cellValue
                                        "仓库说明" -> imPosition.remarks = cellValue
                                        "仓库运营方" -> imPosition.manager = cellValue
                                        "仓库类型" -> imPosition.type = cellValue
                                        "仓库收书地址及联系方式" -> imPosition.address = cellValue
                                    }
                                }
                                imPositionRepository.insertPosition(
                                    code = imPosition.code,
                                    name = imPosition.name,
                                    remarks = imPosition.remarks,
                                    manager = imPosition.manager,
                                    type = imPosition.type,
                                    address = imPosition.address,
                                    id = null
                                )
                            }
                            // 4.1 仓库人员分配中间表-->业务员、仓库人员分配-工作簿2  √√√√√√
                            IM_POSITION_USERS_HEADERS -> {
                                val imPositionUser = ImPositionUser()
                                // 使用headerIndexMap来获取正确的列位置
                                headerIndexMap.forEach { (headerName, columnIndex) ->
                                    val cellValue =
                                        excelDealUtils.getCellValueAsString(row.getCell(columnIndex), evaluator)
                                    when (headerName) {
                                        "职员编码" -> imPositionUser.userCode = cellValue
                                        "职员姓名" -> imPositionUser.userName = cellValue
                                        "仓库编码" -> imPositionUser.positionCode = cellValue
                                        "仓库名称" -> imPositionUser.positionName = cellValue
                                    }
                                }
                                imPositionUserRepository.insertPositionUser(
                                    userCode = imPositionUser.userCode,
                                    userName = imPositionUser.userName,
                                    positionCode = imPositionUser.positionCode,
                                    positionName = imPositionUser.positionName,
                                    userId = null,
                                    positionId = null
                                )
                            }
                            // 5.往来单位中间表-->往来单位档案 √√√√
                            CUSTOMER_HEADERS -> {
                                val imCustomer = ImCustomer()
                                // 使用headerIndexMap来获取正确的列位置
                                headerIndexMap.forEach { (headerName, columnIndex) ->
                                    val cellValue =
                                        excelDealUtils.getCellValueAsString(row.getCell(columnIndex), evaluator)
                                    when (headerName) {
                                        "单位/个人编码" -> imCustomer.code = cellValue
                                        "单位/个人名称" -> imCustomer.name = cellValue
                                        "单位/个人简称" -> imCustomer.abbr = cellValue
                                        "单位分类" -> imCustomer.catalog = cellValue
                                        "朝代" -> imCustomer.dynasty = cellValue
                                        "是否内部单位" -> imCustomer.inUnit = cellValue
                                        "单位性质1" -> imCustomer.nature1 = cellValue
                                        "单位性质2" -> imCustomer.nature2 = cellValue
                                        "单位类型" -> imCustomer.customerType = cellValue
                                        "所属地区" -> imCustomer.area = cellValue
                                        "证件类型" -> imCustomer.cardType = cellValue
                                        "证件号码" -> imCustomer.cardNo = cellValue
                                        "函证联系人" -> imCustomer.correspondenceContact = cellValue
                                        "函证人电话" -> imCustomer.correspondenceTel = cellValue
                                        "函证地址" -> imCustomer.correspondenceAddress = cellValue
                                        "账户名称" -> imCustomer.accountName = cellValue
                                        "银行账户" -> imCustomer.accountNo = cellValue
                                        "开户行" -> imCustomer.bankName = cellValue
                                    }
                                }
                                imCustomerRepository.insertCustomer(
                                    code = imCustomer.code,
                                    name = imCustomer.name,
                                    abbr = imCustomer.abbr,
                                    catalog = imCustomer.catalog,
                                    dynasty = imCustomer.dynasty,
                                    inUnit = imCustomer.inUnit,
                                    nature1 = imCustomer.nature1,
                                    nature2 = imCustomer.nature2,
                                    customerType = imCustomer.customerType,
                                    area = imCustomer.area,
                                    cardType = imCustomer.cardType,
                                    cardNo = imCustomer.cardNo,
                                    correspondenceContact = imCustomer.correspondenceContact,
                                    correspondenceTel = imCustomer.correspondenceTel,
                                    correspondenceAddress = imCustomer.correspondenceAddress,
                                    accountName = imCustomer.accountName,
                                    accountNo = imCustomer.accountNo,
                                    bankName = imCustomer.bankName,
                                    id = null,
                                    topBankId = null,
                                    areaId = null,
                                    customerTypeId = null,
                                    cardTypeId = null
                                )
                            }
                            // 5.往来单位中间表->往来单位 业务员
                            CUSTOMER_BUSINESS_SET_HEADERS -> {
                                val imCustomerBusinessSet = ImCustomerBusinessSet()
                                // 使用headerIndexMap来获取正确的列位置
                                headerIndexMap.forEach { (headerName, columnIndex) ->
                                    val cellValue =
                                        excelDealUtils.getCellValueAsString(row.getCell(columnIndex), evaluator)
                                    when (headerName) {
                                        "往来单位编码" -> imCustomerBusinessSet.customerCode = cellValue
                                        "往来单位名称" -> imCustomerBusinessSet.customerName = cellValue
                                        "采购员编码" -> imCustomerBusinessSet.purchaseUserCode = cellValue
                                        "采购员" -> imCustomerBusinessSet.purchaseUserName = cellValue
                                        "采购部门编码" -> imCustomerBusinessSet.purchaseDepartmentCode = cellValue
                                        "采购部门名称" -> imCustomerBusinessSet.purchaseDepartmentName = cellValue
                                        "销售员编码" -> imCustomerBusinessSet.saleUserCode = cellValue
                                        "销售员" -> imCustomerBusinessSet.saleUserName = cellValue
                                        "销售部门编码" -> imCustomerBusinessSet.saleDepartmentCode = cellValue
                                        "销售部门名称" -> imCustomerBusinessSet.saleDepartmentName = cellValue
                                        "备注" -> imCustomerBusinessSet.remarks = cellValue
                                    }
                                }
                                imCustomerBusinessSetRepository.insertCustomerInfo(
                                    customerCode = imCustomerBusinessSet.customerCode,
                                    customerName = imCustomerBusinessSet.customerName,
                                    purchaseUserCode = imCustomerBusinessSet.purchaseUserCode,
                                    purchaseUserName = imCustomerBusinessSet.purchaseUserName,
                                    purchaseDepartmentCode = imCustomerBusinessSet.purchaseDepartmentCode,
                                    purchaseDepartmentName = imCustomerBusinessSet.purchaseDepartmentName,
                                    saleUserCode = imCustomerBusinessSet.saleUserCode,
                                    saleUserName = imCustomerBusinessSet.saleUserName,
                                    saleDepartmentCode = imCustomerBusinessSet.saleDepartmentCode,
                                    saleDepartmentName = imCustomerBusinessSet.saleDepartmentName,
                                    remarks = imCustomerBusinessSet.remarks,
                                    customerId = null,
                                    purchaseUserId = null,
                                    purchaseDepartmentId = null,
                                    saleUserId = null,
                                    saleDepartmentId = null
                                )
                            }
                            // 6.物品中间表-->存货档案 √√√√
                            ITEM_HEADERS -> {
                                val imItem = ImItem()
                                // 使用headerIndexMap来获取正确的列位置
                                headerIndexMap.forEach { (headerName, columnIndex) ->
                                    val cellValue =
                                        excelDealUtils.getCellValueAsString(row.getCell(columnIndex), evaluator)
                                    when (headerName) {
                                        "物品编码" -> imItem.code = cellValue
                                        "物品名称" -> imItem.name = cellValue
                                        "物品简称" -> imItem.abbr = cellValue
                                        "条形码" -> imItem.barCode = cellValue
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
                                        "正文文字" -> imItem.noteLanguage = cellValue
                                        "内容简介" -> imItem.summary = cellValue
                                        "前言" -> imItem.perface = cellValue
                                        "目录" -> imItem.catalog = cellValue
                                        "书评" -> imItem.bookReview = cellValue
                                        "摘要" -> imItem.bookAbstract = cellValue
                                        "CIP信息" -> imItem.cipInfo = cellValue
                                        "备注" -> imItem.remarks = cellValue
                                        "CIP分类" -> imItem.cipType = cellValue
                                    }

                                }
                                imItemRepository.insertItem(
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
                                    cipType = imItem.cipType,
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
                            // 7.物品性质与科目中间表-->7.凭证科目配置表--工作簿1--财务分类总账科目  √√√√
                            ITEM_NATURE_ACCOUNT_HEADERS -> {
                                val imItemNatureAccount = ImItemNatureAccount()
                                // 使用headerIndexMap来获取正确的列位置
                                headerIndexMap.forEach { (headerName, columnIndex) ->
                                    val cellValue =
                                        excelDealUtils.getCellValueAsString(row.getCell(columnIndex), evaluator)
                                    when (headerName) {
                                        "财务分类" -> imItemNatureAccount.itemNature = cellValue
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
                                }
                                imItemNatureAccountRepository.insertItemNatureAccount(
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
                            // 7.1物品性质与科目中间表-->7.凭证科目配置表--工作簿2--财务分类总账科目  √√√√   工作簿34
                            IM_COST_ITEM_ACCOUNT_HEADERS -> {
                                val imCostItemAccount = ImCostItemAccount()
                                // 使用headerIndexMap来获取正确的列位置
                                headerIndexMap.forEach { (headerName, columnIndex) ->
                                    val cellValue =
                                        excelDealUtils.getCellValueAsString(row.getCell(columnIndex), evaluator)
                                    when (headerName) {
                                        "ERP编码" -> imCostItemAccount.costItemCode = cellValue
                                        "费用项目名称" -> imCostItemAccount.costItemName = cellValue
                                        "上级费用项目" -> imCostItemAccount.parentCostItem = cellValue
                                        "税率" -> imCostItemAccount.tax = cellValue
                                        "备注" -> imCostItemAccount.remarks = cellValue
                                        "生产成本科目" -> imCostItemAccount.protCostAcctCode = cellValue
                                        "应付生产成本科目" -> imCostItemAccount.payProtCostAcctCode = cellValue
                                        "结算应付科目" -> imCostItemAccount.settCopeAcctCode = cellValue
                                        "预付科目" -> imCostItemAccount.prePayAcctCode = cellValue
                                        "支付科目" -> imCostItemAccount.payAcctCode = cellValue
                                        "暂估进项税科目" -> imCostItemAccount.inputTaxAcctCode = cellValue
                                        "进项税已开票" -> imCostItemAccount.inputTaxInvoiceAcctCode = cellValue
                                        "增值税科目" -> imCostItemAccount.valueTaxAcctCode = cellValue
                                        "城市维护建设税科目" -> imCostItemAccount.cityMaintainAcctCode = cellValue
                                        "教育费附加科目" -> imCostItemAccount.educationAcctCode = cellValue
                                        "地方教育费附加科目" -> imCostItemAccount.localEducationAcctCode = cellValue
                                        "劳务税科目" -> imCostItemAccount.laborTaxAcctCode = cellValue
                                        "稿酬税科目" -> imCostItemAccount.royaltiesTaxAcctCode = cellValue
                                    }
                                }
                                imCostItemAccountRepository.insertCostItem(
                                    costItemCode = imCostItemAccount.costItemCode,
                                    costItemName = imCostItemAccount.costItemName,
                                    parentCostItem = imCostItemAccount.parentCostItem,
                                    tax = imCostItemAccount.tax,
                                    remarks = imCostItemAccount.remarks,
                                    protCostAcctCode = imCostItemAccount.protCostAcctCode,
                                    payProtCostAcctCode = imCostItemAccount.payProtCostAcctCode,
                                    settCopeAcctCode = imCostItemAccount.settCopeAcctCode,
                                    prePayAcctCode = imCostItemAccount.prePayAcctCode,
                                    payAcctCode = imCostItemAccount.payAcctCode,
                                    inputTaxAcctCode = imCostItemAccount.inputTaxAcctCode,
                                    inputTaxInvoiceAcctCode = imCostItemAccount.inputTaxInvoiceAcctCode,
                                    valueTaxAcctCode = imCostItemAccount.valueTaxAcctCode,
                                    cityMaintainAcctCode = imCostItemAccount.cityMaintainAcctCode,
                                    educationAcctCode = imCostItemAccount.educationAcctCode,
                                    localEducationAcctCode = imCostItemAccount.localEducationAcctCode,
                                    laborTaxAcctCode = imCostItemAccount.laborTaxAcctCode,
                                    royaltiesTaxAcctCode = imCostItemAccount.royaltiesTaxAcctCode,
                                    costItemId = null,
                                )
                            }
                            // 8 库存期初中间表-->库存期初  √√√√
                            IM_STOCK_INIT_HEADERS -> {
                                val imStockInit = ImStockInit()
                                // 使用headerIndexMap来获取正确的列位置
                                headerIndexMap.forEach { (headerName, columnIndex) ->
                                    val cellValue =
                                        excelDealUtils.getCellValueAsString(row.getCell(columnIndex), evaluator)
                                    when (headerName) {
                                        "物品编码" -> imStockInit.itemCode = cellValue
                                        "物品名称" -> imStockInit.itemName = cellValue
                                        "书号" -> imStockInit.isbn = cellValue
                                        "规格型号" -> imStockInit.spec = cellValue
                                        "仓库编码" -> imStockInit.positionCode = cellValue
                                        "仓库名称" -> imStockInit.positionName = cellValue
                                        "首次入库日期" -> imStockInit.fristInDate = cellValue
                                        "批次（印次）" -> imStockInit.produceNum = cellValue
                                        "结存数量" -> imStockInit.quantity = cellValue
                                        "结存成本单价" -> imStockInit.costPrice = cellValue
                                        "结存金额" -> imStockInit.amount = cellValue
                                    }
                                }
                                imStockInitRepository.insertImStockInit(
                                    itemCode = imStockInit.itemCode,
                                    itemName = imStockInit.itemName,
                                    isbn = imStockInit.isbn,
                                    spec = imStockInit.spec,
                                    positionCode = imStockInit.positionCode,
                                    positionName = imStockInit.positionName,
                                    fristInDate = imStockInit.fristInDate,
                                    produceNum = imStockInit.produceNum,
                                    quantity = imStockInit.quantity,
                                    costPrice = imStockInit.costPrice,
                                    amount = imStockInit.amount,
                                    itemId = null,
                                    positionId = null,
                                    billId = null
                                )
                            }
                            // 9.销售在途中间 表-> 销售在途  √√√√
                            SELL_BILL_HEADERS -> {
                                val imSellBill = ImSellBill()
                                // 使用headerIndexMap来获取正确的列位置
                                headerIndexMap.forEach { (headerName, columnIndex) ->
                                    val cellValue =
                                        excelDealUtils.getCellValueAsString(row.getCell(columnIndex), evaluator)
                                    when (headerName) {
                                        "方向" -> imSellBill.direction = cellValue
                                        "销售订单号" -> imSellBill.orderBillNo = cellValue
                                        "订单日期" -> imSellBill.orderBillDate = cellValue
                                        "销售订单行号" -> imSellBill.orderBillRownum = cellValue
                                        "出库单据号" -> imSellBill.outBillNo = cellValue
                                        "出库单据日期" -> imSellBill.outBillDate = cellValue
                                        "出库单据行号" -> imSellBill.outBillRownum = cellValue
                                        "客户编码" -> imSellBill.customerCode = cellValue
                                        "客户名称" -> imSellBill.customerName = cellValue
                                        "地区" -> imSellBill.area = cellValue
                                        "收货地址" -> imSellBill.receivingAddress = cellValue
                                        "收货人" -> imSellBill.receivingLinkman = cellValue
                                        "收货电话" -> imSellBill.receivingLinkmanTel = cellValue
                                        "订书依据" -> imSellBill.gist = cellValue
                                        "业务员编码" -> imSellBill.userCode = cellValue
                                        "业务员" -> imSellBill.userName = cellValue
                                        "部门编码" -> imSellBill.departmentCode = cellValue
                                        "部门名称" -> imSellBill.departmentName = cellValue
                                        "物品编码" -> imSellBill.itemCode = cellValue
                                        "物品名称" -> imSellBill.itemName = cellValue
                                        "批次" -> imSellBill.produceNum = cellValue
                                        "定价" -> imSellBill.setPrice = cellValue
                                        "计量单位" -> imSellBill.unit = cellValue
                                        "未开票数量" -> imSellBill.noInvoiceQuantity = cellValue
                                        "未开票平均折扣" -> imSellBill.noInvoiceDiscount = cellValue
                                        "未开票码洋" -> imSellBill.noInvoiceAmount = cellValue
                                        "未开票金额（实洋）" -> imSellBill.noInvoiceRealAmount = cellValue
                                        "税率(%)" -> imSellBill.tax = cellValue
                                        "税金(元)" -> imSellBill.taxAmount = cellValue
                                        "仓库编码" -> imSellBill.positionCode = cellValue
                                        "仓库名称" -> imSellBill.positionName = cellValue
                                        "成本单价" -> imSellBill.costPrice = cellValue
                                        "成本金额" -> imSellBill.costAmount = cellValue
                                        "备注" -> imSellBill.remarks = cellValue
                                        "批次的首次入库日期" -> imSellBill.firstInTime = cellValue
                                    }
                                }
                                imSellBillRepository.insertSellBill(
                                    direction = imSellBill.direction,
                                    orderBillNo = imSellBill.orderBillNo,
                                    orderBillDate = imSellBill.orderBillDate,
                                    orderBillRownum = imSellBill.orderBillRownum,
                                    outBillNo = imSellBill.outBillNo,
                                    outBillDate = imSellBill.outBillDate,
                                    outBillRownum = imSellBill.outBillRownum,
                                    customerCode = imSellBill.customerCode,
                                    customerName = imSellBill.customerName,
                                    area = imSellBill.area,
                                    receivingAddress = imSellBill.receivingAddress,
                                    receivingLinkman = imSellBill.receivingLinkman,
                                    receivingLinkmanTel = imSellBill.receivingLinkmanTel,
                                    gist = imSellBill.gist,
                                    userCode = imSellBill.userCode,
                                    userName = imSellBill.userName,
                                    departmentCode = imSellBill.departmentCode,
                                    departmentName = imSellBill.departmentName,
                                    itemCode = imSellBill.itemCode,
                                    itemName = imSellBill.itemName,
                                    produceNum = imSellBill.produceNum,
                                    setPrice = imSellBill.setPrice,
                                    unit = imSellBill.unit,
                                    noInvoiceQuantity = imSellBill.noInvoiceQuantity,
                                    noInvoiceDiscount = imSellBill.noInvoiceDiscount,
                                    noInvoiceAmount = imSellBill.noInvoiceAmount,
                                    noInvoiceRealAmount = imSellBill.noInvoiceRealAmount,
                                    tax = imSellBill.tax,
                                    taxAmount = imSellBill.taxAmount,
                                    positionCode = imSellBill.positionCode,
                                    positionName = imSellBill.positionName,
                                    costPrice = imSellBill.costPrice,
                                    costAmount = imSellBill.costAmount,
                                    remarks = imSellBill.remarks,
                                    firstInTime = imSellBill.firstInTime
                                )
                            }
                            // 10.销售开票中间表 ->销售开票
                            SELL_INVOICE_HEADERS -> {
                                val imSellInvoice = ImSellInvoice()
                                // 使用headerIndexMap来获取正确的列位置
                                headerIndexMap.forEach { (headerName, columnIndex) ->
                                    val cellValue =
                                        excelDealUtils.getCellValueAsString(row.getCell(columnIndex), evaluator)
                                    when (headerName) {
                                        "销售发票单号" -> imSellInvoice.invoiceNo = cellValue
                                        "发票类型" -> imSellInvoice.invoiceType = cellValue
                                        "发票号" -> imSellInvoice.invoiceNumber = cellValue
                                        "销售发票-业务员编码" -> imSellInvoice.invoiceUserCode = cellValue
                                        "销售发票-业务员名称" -> imSellInvoice.invoiceUserName = cellValue
                                        "销售发票-部门编码" -> imSellInvoice.invoiceDepartmentCode = cellValue
                                        "销售发票-部门名称" -> imSellInvoice.invoiceDepartmentName = cellValue
                                        "开票日期" -> imSellInvoice.invoiceDate = cellValue
                                        "开票单位" -> imSellInvoice.invoiceCustomer = cellValue
                                        "销售发票-备注" -> imSellInvoice.remarks = cellValue
                                        "调减金额" -> imSellInvoice.adjustAmount = cellValue
                                        "调减后税金" -> imSellInvoice.adjustTaxAmount = cellValue
                                        "销售订单号" -> imSellInvoice.orderBillNo = cellValue
                                        "订单日期" -> imSellInvoice.orderBillDate = cellValue
                                        "销售订单行号" -> imSellInvoice.orderBillRowNo = cellValue
                                        "出库单据号" -> imSellInvoice.outBillNo = cellValue
                                        "出库单据日期" -> imSellInvoice.outBillDate = cellValue
                                        "出库单据行号" -> imSellInvoice.outBillRowNo = cellValue
                                        "仓库编码" -> imSellInvoice.positionCode = cellValue
                                        "仓库名称" -> imSellInvoice.positionName = cellValue
                                        "客户编码" -> imSellInvoice.customerCode = cellValue
                                        "客户名称" -> imSellInvoice.customerName = cellValue
                                        "地区" -> imSellInvoice.area = cellValue
                                        "收货地址" -> imSellInvoice.receiveAddress = cellValue
                                        "收货人" -> imSellInvoice.receiveMan = cellValue
                                        "收货电话" -> imSellInvoice.receivePhone = cellValue
                                        "订书依据" -> imSellInvoice.gist = cellValue
                                        "销售订单-业务员编码" -> imSellInvoice.orderUserCode = cellValue
                                        "销售订单-业务员名称" -> imSellInvoice.orderUserName = cellValue
                                        "销售订单-部门编码" -> imSellInvoice.orderDepartmentCode = cellValue
                                        "销售订单-部门名称" -> imSellInvoice.orderDepartmentName = cellValue
                                        "物品编码" -> imSellInvoice.itemCode = cellValue
                                        "物品名称" -> imSellInvoice.itemName = cellValue
                                        "批次" -> imSellInvoice.produceNum = cellValue
                                        "定价" -> imSellInvoice.setPrice = cellValue
                                        "计量单位" -> imSellInvoice.unit = cellValue
                                        "开票未收款数量" -> imSellInvoice.quantity = cellValue
                                        "开票未收款平均折扣" -> imSellInvoice.discount = cellValue
                                        "开票未收款金额" -> imSellInvoice.amount = cellValue
                                        "税率(%)" -> imSellInvoice.tax = cellValue
                                        "税金(元)" -> imSellInvoice.taxAmount = cellValue
                                        "成本单价" -> imSellInvoice.costPrice = cellValue
                                        "成本金额" -> imSellInvoice.costAmount = cellValue
                                        "记账日期" -> imSellInvoice.accountDate = cellValue
                                        "调减后不含税金额" -> imSellInvoice.allocatedNoTaxAmount = cellValue
                                    }
                                }
                                imSellInvoiceRepository.insertSellInvoice(
                                    invoiceNo = imSellInvoice.invoiceNo,
                                    invoiceType = imSellInvoice.invoiceType,
                                    invoiceNumber = imSellInvoice.invoiceNumber,
                                    invoiceUserCode = imSellInvoice.invoiceUserCode,
                                    invoiceUserName = imSellInvoice.invoiceUserName,
                                    invoiceDepartmentCode = imSellInvoice.invoiceDepartmentCode,
                                    invoiceDepartmentName = imSellInvoice.invoiceDepartmentName,
                                    invoiceDate = imSellInvoice.invoiceDate,
                                    invoiceCustomer = imSellInvoice.invoiceCustomer,
                                    remarks = imSellInvoice.remarks,
                                    adjustAmount = imSellInvoice.adjustAmount,
                                    adjustTaxAmount = imSellInvoice.adjustTaxAmount,
                                    orderBillNo = imSellInvoice.orderBillNo,
                                    orderBillDate = imSellInvoice.orderBillDate,
                                    orderBillRowNo = imSellInvoice.orderBillRowNo,
                                    outBillNo = imSellInvoice.outBillNo,
                                    outBillDate = imSellInvoice.outBillDate,
                                    outBillRowNo = imSellInvoice.outBillRowNo,
                                    positionCode = imSellInvoice.positionCode,
                                    positionName = imSellInvoice.positionName,
                                    customerCode = imSellInvoice.customerCode,
                                    customerName = imSellInvoice.customerName,
                                    area = imSellInvoice.area,
                                    receiveAddress = imSellInvoice.receiveAddress,
                                    receiveMan = imSellInvoice.receiveMan,
                                    receivePhone = imSellInvoice.receivePhone,
                                    gist = imSellInvoice.gist,
                                    orderUserCode = imSellInvoice.orderUserCode,
                                    orderUserName = imSellInvoice.orderUserName,
                                    orderDepartmentCode = imSellInvoice.orderDepartmentCode,
                                    orderDepartmentName = imSellInvoice.orderDepartmentName,
                                    itemCode = imSellInvoice.itemCode,
                                    itemName = imSellInvoice.itemName,
                                    produceNum = imSellInvoice.produceNum,
                                    setPrice = imSellInvoice.setPrice,
                                    unit = imSellInvoice.unit,
                                    quantity = imSellInvoice.quantity,
                                    discount = imSellInvoice.discount,
                                    amount = imSellInvoice.amount,
                                    tax = imSellInvoice.tax,
                                    taxAmount = imSellInvoice.taxAmount,
                                    costPrice = imSellInvoice.costPrice,
                                    costAmount = imSellInvoice.costAmount,
                                    accountDate = imSellInvoice.accountDate
                                )
                            }
                            // 11. 销售预订单中间表 -> 销售预订单
                            SELL_RESERVE_HEADERS -> {
                                val imSellReserve = ImSellReserve()
                                // 使用headerIndexMap来获取正确的列位置
                                headerIndexMap.forEach { (headerName, columnIndex) ->
                                    val cellValue =
                                        excelDealUtils.getCellValueAsString(row.getCell(columnIndex), evaluator)
                                    when (headerName) {
                                        "单据编号" -> imSellReserve.billNo = cellValue
                                        "单位编码" -> imSellReserve.customerCode = cellValue
                                        "客户单位" -> imSellReserve.customerName = cellValue
                                        "业务日期" -> imSellReserve.date = cellValue
                                        "业务部门编码" -> imSellReserve.departmentCode = cellValue
                                        "业务部门" -> imSellReserve.departmentName = cellValue
                                        "业务员编码" -> imSellReserve.userCode = cellValue
                                        "业务员" -> imSellReserve.userName = cellValue
                                        "是否开票" -> imSellReserve.isInvoice = cellValue
                                        "发票类型" -> imSellReserve.invoiceType = cellValue
                                        "发票号" -> imSellReserve.invoiceNo = cellValue
                                        "开票日期" -> imSellReserve.invoiceDate = cellValue
                                        "开票单位" -> imSellReserve.invoiceCustomer = cellValue
                                        "合同号" -> imSellReserve.contractNo = cellValue
                                        "收款日期" -> imSellReserve.receiveDate = cellValue
                                        "物品编码" -> imSellReserve.itemCode = cellValue
                                        "物品名称" -> imSellReserve.itemName = cellValue
                                        "数量" -> imSellReserve.quantity = cellValue
                                        "不含税金额" -> imSellReserve.amount = cellValue
                                        "税率" -> imSellReserve.tax = cellValue
                                        "税额" -> imSellReserve.taxAmount = cellValue
                                        "金额合计" -> imSellReserve.realAmount = cellValue
                                        "备注" -> imSellReserve.remarks = cellValue
                                        "收款金额" -> imSellReserve.inAmount = cellValue
                                    }
                                }
                                imSellReserveRepository.insertSellReserve(
                                    billNo = imSellReserve.billNo,
                                    customerCode = imSellReserve.customerCode,
                                    customerName = imSellReserve.customerName,
                                    date = imSellReserve.date,
                                    departmentCode = imSellReserve.departmentCode,
                                    departmentName = imSellReserve.departmentName,
                                    userCode = imSellReserve.userCode,
                                    userName = imSellReserve.userName,
                                    isInvoice = imSellReserve.isInvoice,
                                    invoiceType = imSellReserve.invoiceType,
                                    invoiceNo = imSellReserve.invoiceNo,
                                    invoiceDate = imSellReserve.invoiceDate,
                                    invoiceCustomer = imSellReserve.invoiceCustomer,
                                    contractNo = imSellReserve.contractNo,
                                    receiveDate = imSellReserve.receiveDate,
                                    itemCode = imSellReserve.itemCode,
                                    itemName = imSellReserve.itemName,
                                    topicApply = imSellReserve.topicApply,
                                    quantity = imSellReserve.quantity,
                                    amount = imSellReserve.amount,
                                    tax = imSellReserve.tax,
                                    taxAmount = imSellReserve.taxAmount,
                                    realAmount = imSellReserve.realAmount,
                                    remarks = imSellReserve.remarks,
                                    inAmount = imSellReserve.inAmount
                                )
                            }
                            // 12. 采购发票中间表-> 采购发票未付款
                            PURCHASE_INVOICE_HEADERS -> {
                                val imPurchaseInvoice = ImPurchaseInvoice()
                                headerIndexMap.forEach { (headerName, columnIndex) ->
                                    val cellValue =
                                        excelDealUtils.getCellValueAsString(row.getCell(columnIndex), evaluator)
                                    when (headerName) {
                                        "方向" -> imPurchaseInvoice.remarks = cellValue
                                        "采购订单号" -> imPurchaseInvoice.invoiceBillNo = cellValue
                                        "采购订单日期" -> imPurchaseInvoice.invoiceDate = cellValue
                                        "采购订单行号" -> imPurchaseInvoice.invoiceType = cellValue
                                        "入库单据号" -> imPurchaseInvoice.remarks = cellValue
                                        "入库单据日期" -> imPurchaseInvoice.remarks = cellValue
                                        "入库单据行号" -> imPurchaseInvoice.remarks = cellValue
                                        "供应商编码" -> imPurchaseInvoice.remarks = cellValue
                                        "供应商名称" -> imPurchaseInvoice.remarks = cellValue
                                        "地区" -> imPurchaseInvoice.remarks = cellValue
                                        "发货地址" -> imPurchaseInvoice.remarks = cellValue
                                        "发货人" -> imPurchaseInvoice.remarks = cellValue
                                        "发货电话" -> imPurchaseInvoice.remarks = cellValue
                                        "订书依据" -> imPurchaseInvoice.remarks = cellValue
                                        "业务员编码" -> imPurchaseInvoice.remarks = cellValue
                                        "业务员名称" -> imPurchaseInvoice.remarks = cellValue
                                        "部门编码" -> imPurchaseInvoice.remarks = cellValue
                                        "部门名称" -> imPurchaseInvoice.remarks = cellValue
                                        "物品编码" -> imPurchaseInvoice.remarks = cellValue
                                        "物品名称" -> imPurchaseInvoice.remarks = cellValue
                                        "批次" -> imPurchaseInvoice.remarks = cellValue
                                        "定价" -> imPurchaseInvoice.remarks = cellValue
                                        "计量单位" -> imPurchaseInvoice.remarks = cellValue
                                        "未开票数量" -> imPurchaseInvoice.remarks = cellValue
                                        "未开票平均折扣" -> imPurchaseInvoice.remarks = cellValue
                                        "未开票金额" -> imPurchaseInvoice.remarks = cellValue
                                        "税率" -> imPurchaseInvoice.remarks = cellValue
                                        "税金" -> imPurchaseInvoice.remarks = cellValue
                                        "仓库编码" -> imPurchaseInvoice.remarks = cellValue
                                        "仓库名称" -> imPurchaseInvoice.remarks = cellValue
                                        "备注" -> imPurchaseInvoice.remarks = cellValue
                                    }
                                }
                            }
                            // 16.1.1 选题申报-书稿三审-发稿单-书号CIP申请
                            TOPIC_RECORD_HEADERS -> {
                                val imTopicRecord = ImTopicRecord()
                                // 使用headerIndexMap来获取正确的列位置
                                headerIndexMap.forEach { (headerName, columnIndex) ->
                                    val cellValue =
                                        excelDealUtils.getCellValueAsString(row.getCell(columnIndex), evaluator)
                                    when (headerName) {
                                        "选题单号" -> imTopicRecord.topicRecordBillNo = cellValue
                                        "物品编码" -> imTopicRecord.itemCode = cellValue
                                        "物品名称(书名)" -> imTopicRecord.bookName = cellValue
                                        "责任编辑编码" -> imTopicRecord.dutyEditorCode = cellValue
                                        "责任编辑名称" -> imTopicRecord.dutyEditorName = cellValue
                                        "其他编辑" -> imTopicRecord.otherDutyEditor = cellValue
                                        "业务部门编码" -> imTopicRecord.topicRecordDepartmentCode = cellValue
                                        "业务部门名称" -> imTopicRecord.topicRecordDepartmentName = cellValue
                                        "业务日期" -> imTopicRecord.topicRecordBillDate = cellValue
                                        "分卷册名" -> imTopicRecord.partBookName = cellValue
                                        "外文书名" -> imTopicRecord.foreignName = cellValue
                                        "副书名" -> imTopicRecord.viceBookName = cellValue
                                        "丛（套）书名" -> imTopicRecord.seriesName = cellValue
                                        "中图分类" -> imTopicRecord.sinoBookType = cellValue
                                        "正文文种" -> imTopicRecord.noteLanguage = cellValue
                                        "选题申报单正文文字" -> imTopicRecord.language = cellValue
                                        "主要作者" -> imTopicRecord.mainAuthor = cellValue
                                        "书稿字数(千字)" -> imTopicRecord.wordCount = cellValue
                                        "出版类别" -> imTopicRecord.publishType = cellValue
                                        "经营方式" -> imTopicRecord.publishMethod = cellValue
                                        "版次时间-年月" -> imTopicRecord.editionYearMonth = cellValue
                                        "版次" -> imTopicRecord.editionNo = cellValue
                                        "印次时间-年月" -> imTopicRecord.printingYearMonth = cellValue
                                        "印次" -> imTopicRecord.printingNo = cellValue
                                        "开本尺寸名称" -> imTopicRecord.bookFormatSize = cellValue
                                        "开本别名" -> imTopicRecord.bookFormat = cellValue
                                        "印张" -> imTopicRecord.sheetCount = cellValue
                                        "装订方式" -> imTopicRecord.bindingType = cellValue
                                        "印数(册)" -> imTopicRecord.printCount = cellValue
                                        "累计印数(册)" -> imTopicRecord.printCountTotal = cellValue
                                        "定价(元)" -> imTopicRecord.setPrice = cellValue
                                        "成品尺寸(mm)-长" -> imTopicRecord.bookHeight = cellValue
                                        "成品尺寸(mm)-宽" -> imTopicRecord.bookWidth = cellValue
                                        "内容简介（要求200-1000字）" -> imTopicRecord.summary = cellValue
                                        "目标读者" -> imTopicRecord.targetReader = cellValue
                                        "本社同类书比较" -> imTopicRecord.pressSimilarCompare = cellValue
                                        "国内同类书比较" -> imTopicRecord.nationSimilarCompare = cellValue
                                        "营销策略" -> imTopicRecord.sellPolicy = cellValue
                                        "渠道分析" -> imTopicRecord.canalAnaly = cellValue
                                        "重要选题类型" -> imTopicRecord.importantRecordType = cellValue
                                        "合作方" -> imTopicRecord.partner = cellValue
                                        "是否虚拟选题" -> imTopicRecord.virtualBook = cellValue
                                        "是否翻译作品" -> imTopicRecord.translateBook = cellValue
                                        "是否地图" -> imTopicRecord.map = cellValue
                                        "选题年度" -> imTopicRecord.topicYear = cellValue
                                        "选题批次" -> imTopicRecord.produceNum = cellValue
                                        "是否原创" -> imTopicRecord.topicOriginal = cellValue
                                        "是否公版" -> imTopicRecord.publicBook = cellValue
                                        "是否中小学教材" -> imTopicRecord.primaryTextbook = cellValue
                                        "是否中小学教辅" -> imTopicRecord.teachingAuxiliary = cellValue
                                        "是否高校教材" -> imTopicRecord.universityTextbox = cellValue
                                        "是否引进版图书" -> imTopicRecord.introducingBook = cellValue
                                        "引进版图书原书名" -> imTopicRecord.introducingBookName = cellValue
                                        "引进版图书原出版地" -> imTopicRecord.introducingBookAddress = cellValue
                                        "引进版图书原出版者" -> imTopicRecord.introducingBookAuthor = cellValue
                                        "引进版图书外版ISBN" -> imTopicRecord.introducingBookIsbn = cellValue
                                        "引进方式" -> imTopicRecord.introducingBookWay = cellValue
                                        "版权登记号" -> imTopicRecord.introducingBookNo = cellValue
                                        "预计来稿时间" -> imTopicRecord.expectSubmitTime = cellValue
                                        "正文文字" -> imTopicRecord.textLanguage = cellValue
                                        "发行范围" -> imTopicRecord.publishRange = cellValue
                                        "载体形式" -> imTopicRecord.carryForm = cellValue
                                        "图书类型" -> imTopicRecord.bookType = cellValue
                                        "选题申报单-备注" -> imTopicRecord.topicRecordRemarks = cellValue
                                        "三审单号" -> imTopicRecord.thirdTrialBillNo = cellValue
                                        "业务日期(三审)" -> imTopicRecord.thirdTrialBillDate = cellValue
                                        "业务部门编码(三审)" -> imTopicRecord.thirdTrialDepartmentCode = cellValue
                                        "业务部门名称(三审)" -> imTopicRecord.thirdTrialDepartmentName = cellValue
                                        "业务员编码(三审)" -> imTopicRecord.thirdTrialUserCode = cellValue
                                        "业务员名称(三审)" -> imTopicRecord.thirdTrialUserName = cellValue
                                        "选题号" -> imTopicRecord.topicNumber = cellValue
                                        "初审人编码" -> imTopicRecord.firstTrialPersonCode = cellValue
                                        "初审人名称" -> imTopicRecord.firstTrialPersonName = cellValue
                                        "初审日期" -> imTopicRecord.firstTrialDate = cellValue
                                        "初审意见" -> imTopicRecord.firstTrialOpinion = cellValue
                                        "复审人编码" -> imTopicRecord.secondTrialPersonCode = cellValue
                                        "复审人名称" -> imTopicRecord.secondTrialPersonName = cellValue
                                        "复审日期" -> imTopicRecord.secondTrialDate = cellValue
                                        "复审意见" -> imTopicRecord.secondTrialOpinion = cellValue
                                        "终审人编码" -> imTopicRecord.thirdTrialPersonCode = cellValue
                                        "终审人名称" -> imTopicRecord.thirdTrialPersonName = cellValue
                                        "终审日期" -> imTopicRecord.thirdTrialDate = cellValue
                                        "终审意见" -> imTopicRecord.thirdTrialOpinion = cellValue
                                        "发稿单号" -> imTopicRecord.publishBillNo = cellValue
                                        "业务日期(发稿)" -> imTopicRecord.publishBillDate = cellValue
                                        "发稿单印次年月" -> imTopicRecord.publishPrintingYearMonth = cellValue
                                        "发稿单印次" -> imTopicRecord.publishPrintingNo = cellValue
                                        "发稿单业务类型" -> imTopicRecord.publishBusinessType = cellValue
                                        "业务部门编码(发稿)" -> imTopicRecord.publishDepartmentCode = cellValue
                                        "业务部门名称(发稿)" -> imTopicRecord.publishDepartmentName = cellValue
                                        "业务员编码(发稿)" -> imTopicRecord.publishUserCode = cellValue
                                        "业务员名称(发稿)" -> imTopicRecord.publishUserName = cellValue
                                        "出版期间" -> imTopicRecord.publishPeriod = cellValue
                                        "重印物品书号" -> imTopicRecord.reprintItemIsbn = cellValue
                                        "重印物品名称" -> imTopicRecord.reprintItemName = cellValue
                                        "书号和CIP发放单号" -> imTopicRecord.bookNumApplyBillNo = cellValue
                                        "业务日期(书号申请)" -> imTopicRecord.bookNumBillDate = cellValue
                                        "业务部门编码(书号申请)" -> imTopicRecord.bookNumDepartmentCode = cellValue
                                        "业务部门名称(书号申请)" -> imTopicRecord.bookNumDepartmentName = cellValue
                                        "业务员编码(书号申请)" -> imTopicRecord.bookNumUserCode = cellValue
                                        "业务员名称(书号申请)" -> imTopicRecord.bookNumUserName = cellValue
                                        "书号" -> imTopicRecord.isbn = cellValue
                                        "CIP信息" -> imTopicRecord.cipInfo = cellValue
                                        "附加码" -> imTopicRecord.extraCode = cellValue
                                        "cip分类" -> imTopicRecord.cipType = cellValue
                                    }
                                }

                                imTopicRecordRepository.insertTopicRecord(
                                    topicRecordBillNo = imTopicRecord.topicRecordBillNo,
                                    itemCode = imTopicRecord.itemCode,
                                    bookName = imTopicRecord.bookName,
                                    dutyEditorCode = imTopicRecord.dutyEditorCode,
                                    dutyEditorName = imTopicRecord.dutyEditorName,
                                    otherDutyEditor = imTopicRecord.otherDutyEditor,
                                    topicRecordDepartmentCode = imTopicRecord.topicRecordDepartmentCode,
                                    topicRecordDepartmentName = imTopicRecord.topicRecordDepartmentName,
                                    topicRecordBillDate = imTopicRecord.topicRecordBillDate,
                                    partBookName = imTopicRecord.partBookName,
                                    foreignName = imTopicRecord.foreignName,
                                    viceBookName = imTopicRecord.viceBookName,
                                    seriesName = imTopicRecord.seriesName,
                                    sinoBookType = imTopicRecord.sinoBookType,
                                    noteLanguage = imTopicRecord.noteLanguage,
                                    language = imTopicRecord.language,
                                    mainAuthor = imTopicRecord.mainAuthor,
                                    wordCount = imTopicRecord.wordCount,
                                    publishType = imTopicRecord.publishType,
                                    publishMethod = imTopicRecord.publishMethod,
                                    editionYearMonth = imTopicRecord.editionYearMonth,
                                    editionNo = imTopicRecord.editionNo,
                                    printingYearMonth = imTopicRecord.printingYearMonth,
                                    printingNo = imTopicRecord.printingNo,
                                    bookFormatSize = imTopicRecord.bookFormatSize,
                                    bookFormat = imTopicRecord.bookFormat,
                                    sheetCount = imTopicRecord.sheetCount,
                                    bindingType = imTopicRecord.bindingType,
                                    printCount = imTopicRecord.printCount,
                                    printCountTotal = imTopicRecord.printCountTotal,
                                    setPrice = imTopicRecord.setPrice,
                                    bookHeight = imTopicRecord.bookHeight,
                                    bookWidth = imTopicRecord.bookWidth,
                                    summary = imTopicRecord.summary,
                                    targetReader = imTopicRecord.targetReader,
                                    pressSimilarCompare = imTopicRecord.pressSimilarCompare,
                                    nationSimilarCompare = imTopicRecord.nationSimilarCompare,
                                    sellPolicy = imTopicRecord.sellPolicy,
                                    canalAnaly = imTopicRecord.canalAnaly,
                                    importantRecordType = imTopicRecord.importantRecordType,
                                    partner = imTopicRecord.partner,
                                    virtualBook = imTopicRecord.virtualBook,
                                    translateBook = imTopicRecord.translateBook,
                                    map = imTopicRecord.map,
                                    topicYear = imTopicRecord.topicYear,
                                    produceNum = imTopicRecord.produceNum,
                                    topicOriginal = imTopicRecord.topicOriginal,
                                    publicBook = imTopicRecord.publicBook,
                                    primaryTextbook = imTopicRecord.primaryTextbook,
                                    teachingAuxiliary = imTopicRecord.teachingAuxiliary,
                                    universityTextbox = imTopicRecord.universityTextbox,
                                    introducingBook = imTopicRecord.introducingBook,
                                    introducingBookName = imTopicRecord.introducingBookName,
                                    introducingBookAddress = imTopicRecord.introducingBookAddress,
                                    introducingBookAuthor = imTopicRecord.introducingBookAuthor,
                                    introducingBookIsbn = imTopicRecord.introducingBookIsbn,
                                    introducingBookWay = imTopicRecord.introducingBookWay,
                                    introducingBookNo = imTopicRecord.introducingBookNo,
                                    expectSubmitTime = imTopicRecord.expectSubmitTime,
                                    textLanguage = imTopicRecord.textLanguage,
                                    publishRange = imTopicRecord.publishRange,
                                    carryForm = imTopicRecord.carryForm,
                                    bookType = imTopicRecord.bookType,
                                    topicRecordRemarks = imTopicRecord.topicRecordRemarks,
                                    thirdTrialBillNo = imTopicRecord.thirdTrialBillNo,
                                    thirdTrialBillDate = imTopicRecord.thirdTrialBillDate,
                                    thirdTrialDepartmentCode = imTopicRecord.thirdTrialDepartmentCode,
                                    thirdTrialDepartmentName = imTopicRecord.thirdTrialDepartmentName,
                                    thirdTrialUserCode = imTopicRecord.thirdTrialUserCode,
                                    thirdTrialUserName = imTopicRecord.thirdTrialUserName,
                                    topicNumber = imTopicRecord.topicNumber,
                                    firstTrialPersonCode = imTopicRecord.firstTrialPersonCode,
                                    firstTrialPersonName = imTopicRecord.firstTrialPersonName,
                                    firstTrialDate = imTopicRecord.firstTrialDate,
                                    firstTrialOpinion = imTopicRecord.firstTrialOpinion,
                                    secondTrialPersonCode = imTopicRecord.secondTrialPersonCode,
                                    secondTrialPersonName = imTopicRecord.secondTrialPersonName,
                                    secondTrialDate = imTopicRecord.secondTrialDate,
                                    secondTrialOpinion = imTopicRecord.secondTrialOpinion,
                                    thirdTrialPersonCode = imTopicRecord.thirdTrialPersonCode,
                                    thirdTrialPersonName = imTopicRecord.thirdTrialPersonName,
                                    thirdTrialDate = imTopicRecord.thirdTrialDate,
                                    thirdTrialOpinion = imTopicRecord.thirdTrialOpinion,
                                    publishBillNo = imTopicRecord.publishBillNo,
                                    publishBillDate = imTopicRecord.publishBillDate,
                                    publishPrintingYearMonth = imTopicRecord.publishPrintingYearMonth,
                                    publishPrintingNo = imTopicRecord.publishPrintingNo,
                                    publishBusinessType = imTopicRecord.publishBusinessType,
                                    publishDepartmentCode = imTopicRecord.publishDepartmentCode,
                                    publishDepartmentName = imTopicRecord.publishDepartmentName,
                                    publishUserCode = imTopicRecord.publishUserCode,
                                    publishUserName = imTopicRecord.publishUserName,
                                    publishPeriod = imTopicRecord.publishPeriod,
                                    reprintItemIsbn = imTopicRecord.reprintItemIsbn,
                                    reprintItemName = imTopicRecord.reprintItemName,
                                    bookNumApplyBillNo = imTopicRecord.bookNumApplyBillNo,
                                    bookNumBillDate = imTopicRecord.bookNumBillDate,
                                    bookNumDepartmentCode = imTopicRecord.bookNumDepartmentCode,
                                    bookNumDepartmentName = imTopicRecord.bookNumDepartmentName,
                                    bookNumUserCode = imTopicRecord.bookNumUserCode,
                                    bookNumUserName = imTopicRecord.bookNumUserName,
                                    isbn = imTopicRecord.isbn,
                                    topicRecordDepartmentId = imTopicRecord.topicRecordDepartmentId,
                                    cipInfo = imTopicRecord.cipInfo,
                                    extraCode = imTopicRecord.extraCode,
                                    cipType = imTopicRecord.cipType,
                                    bookNumSinoBookType = imTopicRecord.bookNumSinoBookType,
                                    organId = organId
                                )
                            }
                            // 16.1.2 作者信息
                            TOPIC_RECORD_AUTHOR_HEADERS -> {
                                val imTopicRecordAuthor = ImTopicRecordAuthor()
                                // 使用headerIndexMap来获取正确的列位置
                                headerIndexMap.forEach { (headerName, columnIndex) ->
                                    val cellValue =
                                        excelDealUtils.getCellValueAsString(row.getCell(columnIndex), evaluator)
                                    when (headerName) {
                                        "选题单号" -> imTopicRecordAuthor.topicRecordBillNo = cellValue
                                        "书名" -> imTopicRecordAuthor.bookName = cellValue
                                        "作者编码" -> imTopicRecordAuthor.authorCode = cellValue
                                        "作者姓名" -> imTopicRecordAuthor.authorName = cellValue
                                        "主要作者" -> imTopicRecordAuthor.mainAuthor = cellValue
                                        "著作方式" -> imTopicRecordAuthor.writeType = cellValue
                                        "所在单位" -> imTopicRecordAuthor.authorCompany = cellValue
                                        "职称" -> imTopicRecordAuthor.authorTitle = cellValue
                                        "作者简介" -> imTopicRecordAuthor.majorWorks = cellValue
                                        "作者背景审查情况" -> imTopicRecordAuthor.backgroundDetail = cellValue
                                        "作者已出版书市场情况" -> imTopicRecordAuthor.marketConditions = cellValue
                                        "备注" -> imTopicRecordAuthor.remarks = cellValue
                                        "国籍" -> imTopicRecordAuthor.nation = cellValue
                                        "朝代" -> imTopicRecordAuthor.dynasty = cellValue

                                    }
                                }
                                imTopicRecordAuthorRepository.insertTopicRecordAuthor(
                                    topicRecordBillNo = imTopicRecordAuthor.topicRecordBillNo,
                                    bookName = imTopicRecordAuthor.bookName,
                                    authorCode = imTopicRecordAuthor.authorCode,
                                    authorName = imTopicRecordAuthor.authorName,
                                    mainAuthor = imTopicRecordAuthor.mainAuthor,
                                    writeType = imTopicRecordAuthor.writeType,
                                    authorCompany = imTopicRecordAuthor.authorCompany,
                                    authorTitle = imTopicRecordAuthor.authorTitle,
                                    majorWorks = imTopicRecordAuthor.majorWorks,
                                    backgroundDetail = imTopicRecordAuthor.backgroundDetail,
                                    marketConditions = imTopicRecordAuthor.marketConditions,
                                    remarks = imTopicRecordAuthor.remarks,
                                    nation = imTopicRecordAuthor.nation,
                                    dynasty = imTopicRecordAuthor.dynasty,
                                    organId = organId
                                )
                            }
                            // 16.2费用预估-结算-付款
                            FEE_BILL_HEADERS -> {
                                val imFeeBill = ImFeeBill()
                                // 使用headerIndexMap来获取正确的列位置
                                headerIndexMap.forEach { (headerName, columnIndex) ->
                                    val cellValue =
                                        excelDealUtils.getCellValueAsString(row.getCell(columnIndex), evaluator)
                                    when (headerName) {
                                        "物品编码" -> imFeeBill.itemCode = cellValue
                                        "物品名称" -> imFeeBill.itemName = cellValue
                                        "印次" -> imFeeBill.printingNo = cellValue
                                        "预估单号" -> imFeeBill.feeEstimateBillNo = cellValue
                                        "预估单行号" -> imFeeBill.feeEstimateRowNum = cellValue
                                        "费用项目" -> imFeeBill.costItem = cellValue
                                        "往来单位编码" -> imFeeBill.customerCode = cellValue
                                        "往来单位" -> imFeeBill.customerName = cellValue
                                        "业务日期" -> imFeeBill.estimateDate = cellValue
                                        "记账日期" -> imFeeBill.estimateVoucherDate = cellValue
                                        "业务部门编码" -> imFeeBill.estimateDepartmentCode = cellValue
                                        "业务部门" -> imFeeBill.estimateDepartmentName = cellValue
                                        "业务员编码" -> imFeeBill.estimateUserCode = cellValue
                                        "业务员" -> imFeeBill.estimateUserName = cellValue
                                        "自备材料金额" -> imFeeBill.selfMaterialAmount = cellValue
                                        "预估金额(元)" -> imFeeBill.estimateAmount = cellValue
                                        "税率(%)" -> imFeeBill.estimateTax = cellValue
                                        "暂估税金(元)" -> imFeeBill.estimateTaxAmount = cellValue
                                        "结算单号" -> imFeeBill.feeSettleBillNo = cellValue
                                        "结算单行号" -> imFeeBill.feeSettleRowNum = cellValue
                                        "往来单位编码（结算）" -> imFeeBill.settleCustomerCode = cellValue
                                        "往来单位（结算）" -> imFeeBill.settleCustomerName = cellValue
                                        "业务日期（结算）" -> imFeeBill.settleDate = cellValue
                                        "记账日期（结算）" -> imFeeBill.settleVoucherDate = cellValue
                                        "业务部门编码（结算）" -> imFeeBill.settleDepartmentCode = cellValue
                                        "业务部门（结算）" -> imFeeBill.settleDepartmentName = cellValue
                                        "业务员编码（结算）" -> imFeeBill.settleUserCode = cellValue
                                        "业务员（结算）" -> imFeeBill.settleUserName = cellValue
                                        "本次结算金额(元)" -> imFeeBill.settleAmount = cellValue
                                        "税率(%)（结算）" -> imFeeBill.settleTax = cellValue
                                        "税金(元)" -> imFeeBill.settleTaxAmount = cellValue
                                        "增值税(元）" -> imFeeBill.addValueTax = cellValue
                                        "城建税(元）" -> imFeeBill.urbanConstructTax = cellValue
                                        "教育费附加税(元）" -> imFeeBill.educateAdditionTax = cellValue
                                        "地方教育附加税(元)" -> imFeeBill.localEducateAdditionTax = cellValue
                                        "应纳税所得额(元）" -> imFeeBill.taxableIncome = cellValue
                                        "其他扣款金额(元）" -> imFeeBill.otherAmount = cellValue
                                        "扣款原因" -> imFeeBill.otherReason = cellValue
                                        "发票类型" -> imFeeBill.invoiceType = cellValue
                                        "发票号" -> imFeeBill.invoiceNumber = cellValue
                                        "是否结算完成" -> imFeeBill.settleCompletion = cellValue
                                        "开户行" -> imFeeBill.customerAccountBankName = cellValue
                                        "开户行分行" -> imFeeBill.customerBankName = cellValue
                                        "账户名称" -> imFeeBill.customerAccountName = cellValue
                                        "银行账号（结算）" -> imFeeBill.customerAccountNo = cellValue
                                        "付款方开户银行（结算）" -> imFeeBill.organBankName = cellValue
                                        "付款方银行账号（结算）" -> imFeeBill.organAccountNo = cellValue
                                        "备注" -> imFeeBill.remarks = cellValue
                                        "付款单号" -> imFeeBill.payBillNo = cellValue
                                        "付款单行号" -> imFeeBill.payRowNum = cellValue
                                        "业务日期（付款）" -> imFeeBill.payDate = cellValue
                                        "记账日期（付款）" -> imFeeBill.payVoucherDate = cellValue
                                        "业务部门编码（付款）" -> imFeeBill.payDepartmentCode = cellValue
                                        "业务部门（付款）" -> imFeeBill.payDepartmentName = cellValue
                                        "业务员编码（付款）" -> imFeeBill.payUserCode = cellValue
                                        "业务员（付款）" -> imFeeBill.payUserName = cellValue
                                        "本次支付金额(元)" -> imFeeBill.payAmount = cellValue
                                        "税金(元)（付款）" -> imFeeBill.payTaxAmount = cellValue
                                    }
                                }
                                imFeeBillRepository.insertFeeBill(
                                    itemCode = imFeeBill.itemCode,
                                    itemName = imFeeBill.itemName,
                                    printingNo = imFeeBill.printingNo,
                                    feeEstimateBillNo = imFeeBill.feeEstimateBillNo,
                                    feeEstimateRowNum = imFeeBill.feeEstimateRowNum,
                                    costItem = imFeeBill.costItem,
                                    customerCode = imFeeBill.customerCode,
                                    customerName = imFeeBill.customerName,
                                    estimateDate = imFeeBill.estimateDate,
                                    estimateVoucherDate = imFeeBill.estimateVoucherDate,
                                    estimateDepartmentCode = imFeeBill.estimateDepartmentCode,
                                    estimateDepartmentName = imFeeBill.estimateDepartmentName,
                                    estimateUserCode = imFeeBill.estimateUserCode,
                                    estimateUserName = imFeeBill.estimateUserName,
                                    selfMaterialAmount = imFeeBill.selfMaterialAmount,
                                    estimateAmount = imFeeBill.estimateAmount,
                                    estimateTax = imFeeBill.estimateTax,
                                    estimateTaxAmount = imFeeBill.estimateTaxAmount,
                                    feeSettleBillNo = imFeeBill.feeSettleBillNo,
                                    feeSettleRowNum = imFeeBill.feeSettleRowNum,
                                    settleCustomerCode = imFeeBill.settleCustomerCode,
                                    settleCustomerName = imFeeBill.settleCustomerName,
                                    settleDate = imFeeBill.settleDate,
                                    settleVoucherDate = imFeeBill.settleVoucherDate,
                                    settleDepartmentCode = imFeeBill.settleDepartmentCode,
                                    settleDepartmentName = imFeeBill.settleDepartmentName,
                                    settleUserCode = imFeeBill.settleUserCode,
                                    settleUserName = imFeeBill.settleUserName,
                                    settleAmount = imFeeBill.settleAmount,
                                    settleTax = imFeeBill.settleTax,
                                    settleTaxAmount = imFeeBill.settleTaxAmount,
                                    addValueTax = imFeeBill.addValueTax,
                                    urbanConstructTax = imFeeBill.urbanConstructTax,
                                    educateAdditionTax = imFeeBill.educateAdditionTax,
                                    localEducateAdditionTax = imFeeBill.localEducateAdditionTax,
                                    taxableIncome = imFeeBill.taxableIncome,
                                    otherAmount = imFeeBill.otherAmount,
                                    otherReason = imFeeBill.otherReason,
                                    invoiceType = imFeeBill.invoiceType,
                                    invoiceNumber = imFeeBill.invoiceNumber,
                                    settleCompletion = imFeeBill.settleCompletion,
                                    customerAccountBankName = imFeeBill.customerAccountBankName,
                                    customerBankName = imFeeBill.customerBankName,
                                    customerAccountName = imFeeBill.customerAccountName,
                                    customerAccountNo = imFeeBill.customerAccountNo,
                                    organBankName = imFeeBill.organBankName,
                                    organAccountNo = imFeeBill.organAccountNo,
                                    remarks = imFeeBill.remarks,
                                    payBillNo = imFeeBill.payBillNo,
                                    payRowNum = imFeeBill.payRowNum,
                                    payDate = imFeeBill.payDate,
                                    payVoucherDate = imFeeBill.payVoucherDate,
                                    payDepartmentCode = imFeeBill.payDepartmentCode,
                                    payDepartmentName = imFeeBill.payDepartmentName,
                                    payUserCode = imFeeBill.payUserCode,
                                    payUserName = imFeeBill.payUserName,
                                    payAmount = imFeeBill.payAmount,
                                    payTaxAmount = imFeeBill.payTaxAmount,
                                    organId = organId
                                )
                            }

                            OTHERS_SKIP_HEADERS -> {
                                continue
                            }
                        }                // 进度计算：10% - 90%
                        val progress = 10 + ((j + 1) * 80 / totalRows)
                        val message = "正在处理 ($currentFileIndex/$totalFiles) - 第 ${j + 1}/$totalRows 行"

                        updateTaskStatus(taskId, "processing", progress, message)

                        // 每处理5行或最后一行时发送进度更新
                        if ((j + 1) % 5 == 0 || j == totalRows - 1) {
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
                    } catch (e: Exception) {
                        // 记录错误行的详细数据
                        val rowData = formatRowData(row, headerIndexMap, evaluator)
                        logger.error(
                            """
                            |处理Excel数据失败:
                            |文件名: $fileName
                            |工作表: ${sheet.sheetName}
                            |行号: ${i + 1}
                            |数据内容: 
                            |$rowData
                            |错误信息: ${e.message}
                            |""".trimMargin()
                        )

                        // 重新抛出异常以触发事务回滚
                        throw Exception("处理第 ${i + 1} 行数据失败: ${e.message}", e)
                    }
                }

            }
            //处理完成根据表头调用存储过程
//                when (matchedHeaderType) {
//                    DEPARTMENT_HEADERS -> {
//                        //导入前 清除该类型的数据
//                        imImportResultRepository.deleteByType("部门")
//                        //部门 导入检查
//                        if (oraclePackageService.importDepartment(organId) == 1) {
//                            throw ImportDataException("部门 数据处理失败")
//                        }
//                        updateTaskStatus(taskId, "completed", 100, "部门导入完成")
//                        webSocketHandler.sendToSession(
//                            sessionId, ProgressData(
//                                type = "completed",
//                                taskId = taskId,
//                                fileName = fileName,
//                                progress = 100,
//                                status = "completed",
//                                message = "部门导入完成"
//                            )
//                        )
//                    }
//
//                    DEPARTMENT_TYPE_HEADERS -> {
//                        //导入前 清除该类型的数据
//                        imImportResultRepository.deleteByType("部门类型")
//                        //部门 类型导入
//                        if (oraclePackageService.importDepartmentType() == 1) {
//                            throw ImportDataException("部门类型数据处理失败")
//                        }
//                        println("部门类型导入检查")
//                        updateTaskStatus(taskId, "completed", 100, "部门类型导入完成")
//                        webSocketHandler.sendToSession(
//                            sessionId, ProgressData(
//                                type = "completed",
//                                taskId = taskId,
//                                fileName = fileName,
//                                progress = 100,
//                                status = "completed",
//                                message = "部门类型导入完成"
//                            )
//                        )
//                    }
//
//                    USER_HEADERS -> {
//                        //导入前 清除该类型的数据
//                        imImportResultRepository.deleteByType("职员")
//                        //职员导入
//                        if (oraclePackageService.importUser(organId) == 1) {
//                            throw ImportDataException("职员数据处理失败")
//                        }
//                        updateTaskStatus(taskId, "completed", 100, "职员导入完成")
//                        webSocketHandler.sendToSession(
//                            sessionId, ProgressData(
//                                type = "completed",
//                                taskId = taskId,
//                                fileName = fileName,
//                                progress = 100,
//                                status = "completed",
//                                message = "职员导入完成"
//                            )
//                        )
//                    }
//
//                    ROLE_HEADERS -> {
//                        //导入前 清除该类型的数据
//                        imImportResultRepository.deleteByType("角色")
//                        //系统角色表导入
//                        if (oraclePackageService.importRole(organId) == 1) {
//                            throw ImportDataException("角色数据处理失败")
//                        }
//                        updateTaskStatus(taskId, "completed", 100, "系统角色导入完成")
//                        webSocketHandler.sendToSession(
//                            sessionId, ProgressData(
//                                type = "completed",
//                                taskId = taskId,
//                                fileName = fileName,
//                                progress = 100,
//                                status = "completed",
//                                message = "系统角色导入完成"
//                            )
//                        )
//                    }
//
//                    USER_ROLE_HEADERS -> {
//                        //导入前 清除该类型的数据
//                        imImportResultRepository.deleteByType("职员角色")
//                        //职员角色表导入
//                        if (oraclePackageService.importUserRole(organId) == 1) {
//                            throw ImportDataException("职员角色数据处理失败")
//                        }
//                        updateTaskStatus(taskId, "completed", 100, "职员角色导入完成")
//                        webSocketHandler.sendToSession(
//                            sessionId, ProgressData(
//                                type = "completed",
//                                taskId = taskId,
//                                fileName = fileName,
//                                progress = 100,
//                                status = "completed",
//                                message = "职员角色导入完成"
//                            )
//                        )
//                    }
//
//                    POSITION_HEADERS -> {
//                        //导入前 清除该类型的数据
//                        imImportResultRepository.deleteByType("货位")
//                        //职员角色表导入
//                        if (oraclePackageService.importPosition(organId) == 1) {
//                            throw ImportDataException("货位数据处理失败")
//                        }
//                        updateTaskStatus(taskId, "completed", 100, "货位导入完成")
//                        webSocketHandler.sendToSession(
//                            sessionId, ProgressData(
//                                type = "completed",
//                                taskId = taskId,
//                                fileName = fileName,
//                                progress = 100,
//                                status = "completed",
//                                message = "货位导入完成"
//                            )
//                        )
//                    }
//
//                    IM_POSITION_USERS_HEADERS -> {
//
//                    }
//
//                    CUSTOMER_HEADERS -> {
//                        //导入前 清除该类型的数据
//                        imImportResultRepository.deleteByType("单位")
//                        //职员角色表导入
//                        if (oraclePackageService.importCustomer(organId) == 1) {
//                            throw ImportDataException("单位数据处理失败")
//                        }
//                        updateTaskStatus(taskId, "completed", 100, "单位导入完成")
//                        webSocketHandler.sendToSession(
//                            sessionId, ProgressData(
//                                type = "completed",
//                                taskId = taskId,
//                                fileName = fileName,
//                                progress = 100,
//                                status = "completed",
//                                message = "单位导入完成"
//                            )
//                        )
//                    }
//
//                    ITEM_HEADERS -> {
//                    }
//
//                    ITEM_NATURE_ACCOUNT_HEADERS -> {
//
//                    }
//
//                    IM_COST_ITEM_ACCOUNT_HEADERS -> {
//
//                    }
//
//                    IM_STOCK_INIT_HEADERS -> {
//
//                    }
//
//                    SELL_BILL_HEADERS -> {
//
//                    }
//
//                    null -> {
//
//                    }
//
//                    OTHERS_SKIP_HEADERS -> continue
//                }

            //关闭
            workbook.close()

            // 发送完成通知
            val completeMessage = "文件处理完成"
            webSocketHandler.sendToSession(
                sessionId, ProgressData(
                    type = "completed",
                    taskId = taskId,
                    fileName = fileName,
                    progress = 100,
                    status = "completed",
                    message = completeMessage
                )
            )
            updateTaskStatus(taskId, "completed", 100, completeMessage)
            logger.info("文件处理完成: $fileName (taskId: $taskId)")

            return true

        } catch (e: Exception) {
            logger.error("处理文件失败: ${e.message}", e)
            println("处理文件失败: ${e.message}")

            // 发送错误通知
            webSocketHandler.sendToSession(
                sessionId, ProgressData(
                    type = "error",
                    taskId = taskId,
                    fileName = fileName,
                    progress = 100,
                    status = "error",
                    message = when (e) {
                        is NoSuchFileException -> "文件不存在或无法访问: ${e.message}"
                        is DataIntegrityViolationException -> "调用存储过程失败: ${e.message}"
                        else -> "未知错误: ${e.message}"
                    }
                )
            )
            // 更新任务状态
            updateTaskStatus(taskId, "error", 0, "处理失败: ${e.message}")
            throw e
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
     * 格式化行数据为可读的字符串，用于日志记录
     * @param row Excel行对象
     * @param headerIndexMap 表头与列索引的映射
     * @param evaluator 公式计算器
     * @return 格式化的行数据字符串
     */
    private fun formatRowData(row: Row, headerIndexMap: Map<String, Int>, evaluator: FormulaEvaluator): String {
        val sb = StringBuilder()

        headerIndexMap.forEach { (headerName, columnIndex) ->
            val cellValue = excelDealUtils.getCellValueAsString(row.getCell(columnIndex), evaluator)
            sb.append("$headerName: $cellValue\n")
        }

        return sb.toString()
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
    DEPARTMENT_TYPE_HEADERS(
        listOf(
            "顺序号", "部门类型", "备注"
        )
    ),
    USER_HEADERS(
        listOf(
            "职员编码",
            "手机号码",
            "所属部门编码",
            "所属部门名称",
            "性别",
            "能否登录系统",
            "备注",
            "用户属性",
            "查询权限"
        )
    ),
    ROLE_HEADERS(
        listOf(
            "角色编码", "角色名称"
        )
    ),
    USER_ROLE_HEADERS(
        listOf(
            "职员编码",
            "职员名称",
            "角色编码",
            "角色名称"

        )
    ),
    POSITION_HEADERS(
        listOf(
            "仓库编码", "仓库名称", "仓库说明", "仓库运营方", "仓库类型", "仓库收书地址及联系方式"
        )
    ),
    IM_POSITION_USERS_HEADERS(
        listOf(
            "职员编码",
            "职员姓名",
            "手机号",
            "仓库编码",
            "仓库名称"
        )
    ),
    CUSTOMER_HEADERS(
        listOf(
            "单位/个人编码",
            "单位/个人名称",
            "单位/个人简称",
            "单位分类",
            "朝代",
            "是否内部单位",
            "单位性质1",
            "单位性质2",
            "单位类型",
            "所属地区",
            "银行账户",
            "账户名称",
            "开户行",
            "函证联系人",
            "证件类型",
            "证件号码",
            "函证人电话",
            "函证地址"
        )
    ),
    CUSTOMER_BUSINESS_SET_HEADERS(
        listOf(
            "往来单位编码",
            "往来单位名称",
            "采购员编码",
            "采购员",
            "采购部门编码",
            "采购部门名称",
            "销售员编码",
            "销售员",
            "销售部门编码",
            "销售部门名称",
            "备注"
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
            "财务分类",
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
    ),
    IM_COST_ITEM_ACCOUNT_HEADERS(
        listOf(
            "ERP编码 ",
            "费用项目名称",
            "上级费用项目",
            "税率",
            "备注",
            "生产成本科目",
            "应付生产成本科目",
            "结算应付科目",
            "预付科目",
            "支付科目",
            "暂估进项税科目",
            "进项税已开票",
            "增值税科目",
            "城市维护建设税科目",
            "教育费附加科目",
            "地方教育费附加科目",
            "劳务税科目",
            "稿酬税科目",

            )
    ),
    IM_STOCK_INIT_HEADERS(
        listOf(
            "物品编码",
            "物品名称",
            "书号",
            "规格型号",
            "仓库编码",
            "仓库名称",
            "首次入库日期",
            "批次（印次）",
            "结存数量",
            "结存成本单价",
            "结存金额"

        )
    ),
    SELL_BILL_HEADERS(
        listOf(
            "方向",
            "销售订单号",
            "订单日期",
            "销售订单行号",
            "出库单据号",
            "出库单据日期",
            "出库单据行号",
            "客户编码",
            "客户名称",
            "地区",
            "收货地址",
            "收货人",
            "收货电话",
            "订书依据",
            "业务员编码",
            "业务员",
            "部门编码",
            "部门名称",
            "物品编码",
            "物品名称",
            "批次",
            "定价",
            "计量单位",
            "未开票数量",
            "未开票平均折扣",
            "未开票码洋",
            "未开票金额（实洋）",
            "税率(%)",
            "税金(元)",
            "仓库编码",
            "仓库名称",
            "成本单价",
            "成本金额",
            "备注",
            "批次的首次入库日期"
        )
    ),
    SELL_INVOICE_HEADERS(
        listOf(
            "销售发票单号",
            "发票类型",
            "发票号",
            "销售发票-业务员编码",
            "销售发票-业务员名称",
            "销售发票-部门编码",
            "销售发票-部门名称",
            "开票日期",
            "开票单位",
            "销售发票-备注",
            "调减金额",
            "调减后税金",
            "销售订单号",
            "订单日期",
            "销售订单行号",
            "出库单据号",
            "出库单据日期",
            "出库单据行号",
            "仓库编码",
            "仓库名称",
            "客户编码",
            "客户名称",
            "地区",
            "收货地址",
            "收货人",
            "收货电话",
            "订书依据",
            "销售订单-业务员编码",
            "销售订单-业务员名称",
            "销售订单-部门编码",
            "销售订单-部门名称",
            "物品编码",
            "物品名称",
            "批次",
            "定价",
            "计量单位",
            "开票未收款数量",
            "开票未收款平均折扣",
            "开票未收款金额",
            "税率(%)",
            "税金(元)",
            "成本单价",
            "成本金额",
            "记账日期",
            "调减后不含税金额"
        )
    ),
    SELL_RESERVE_HEADERS(
        listOf(
            "单据编号",
            "单位编码",
            "客户单位",
            "业务日期",
            "业务部门编码",
            "业务部门",
            "业务员编码",
            "业务员",
            "是否开票",
            "发票类型",
            "发票号",
            "开票日期",
            "开票单位",
            "合同号",
            "收款日期",
            "物品编码",
            "物品名称",
            "数量",
            "不含税金额",
            "税率",
            "税额",
            "金额合计",
            "备注",
            "收款金额"
        )
    ),
    PURCHASE_INVOICE_HEADERS(
        listOf(
            "方向",
            "采购订单号",
            "采购订单日期",
            "采购订单行号",
            "入库单据号",
            "入库单据日期",
            "入库单据行号",
            "供应商编码",
            "供应商名称",
            "地区",
            "发货地址",
            "发货人",
            "发货电话",
            "订书依据",
            "业务员编码",
            "业务员名称",
            "部门编码",
            "部门名称",
            "物品编码",
            "物品名称",
            "批次",
            "定价",
            "计量单位",
            "未开票数量",
            "未开票平均折扣",
            "未开票金额",
            "税率(%)",
            "税金(元)",
            "仓库编码",
            "仓库名称",
            "备注"
        )
    ),
    TOPIC_RECORD_HEADERS(
        listOf(
            "选题单号",
            "物品编码",
            "物品名称(书名)",
            "责任编辑编码",
            "责任编辑名称",
            "其他编辑",
            "业务部门编码",
            "业务部门名称",
            "业务日期",
            "分卷册名",
            "外文书名",
            "副书名",
            "丛（套）书名",
            "中图分类",
            "正文文种",
            "选题申报单正文文字",
            "主要作者",
            "书稿字数(千字)",
            "出版类别",
            "经营方式",
            "版次时间-年月",
            "版次",
            "印次时间-年月",
            "印次",
            "开本尺寸名称",
            "开本别名",
            "印张",
            "装订方式",
            "印数(册)",
            "累计印数(册)",
            "定价(元)",
            "成品尺寸(mm)-长",
            "成品尺寸(mm)-宽",
            "内容简介（要求200-1000字）",
            "目标读者",
            "本社同类书比较",
            "国内同类书比较",
            "营销策略",
            "渠道分析",
            "重要选题类型",
            "合作方",
            "是否虚拟选题",
            "是否翻译作品",
            "是否地图",
            "选题年度",
            "选题批次",
            "是否原创",
            "是否公版",
            "是否中小学教材",
            "是否中小学教辅",
            "是否高校教材",
            "是否引进版图书",
            "引进版图书原书名",
            "引进版图书原出版地",
            "引进版图书原出版者",
            "引进版图书外版ISBN",
            "引进方式",
            "版权登记号",
            "预计来稿时间",
            "正文文字",
            "发行范围",
            "载体形式",
            "图书类型",
            "选题申报单-备注",
            "*云章选题单状态",
            "三审单号",
            "业务日期(三审)",
            "业务部门编码(三审)",
            "业务部门名称(三审)",
            "业务员编码(三审)",
            "业务员名称(三审)",
            "选题号",
            "初审人编码",
            "初审人名称",
            "初审日期",
            "初审意见",
            "复审人编码",
            "复审人名称",
            "复审日期",
            "复审意见",
            "终审人编码",
            "终审人名称",
            "终审日期",
            "终审意见",
            "发稿单号",
            "业务日期(发稿)",
            "发稿单印次年月",
            "发稿单印次",
            "发稿单业务类型",
            "业务部门编码(发稿)",
            "业务部门名称(发稿)",
            "业务员编码(发稿)",
            "业务员名称(发稿)",
            "出版期间",
            "重印物品书号",
            "重印物品名称",
            "书号和CIP发放单号",
            "业务日期(书号申请)",
            "业务部门编码(书号申请)",
            "业务部门名称(书号申请)",
            "业务员编码(书号申请)",
            "业务员名称(书号申请)",
            "书号",
            "CIP信息",
            "附加码",
            "cip分类",
            "中图分类"
        )
    ),
    TOPIC_RECORD_AUTHOR_HEADERS(
        listOf(
            "选题单号",
            "书名",
            "作者编码",
            "作者姓名",
            "主要作者",
            "著作方式",
            "所在单位",
            "职称",
            "作者简介",
            "作者背景审查情况",
            "作者已出版书市场情况",
            "备注",
            "国籍",
            "朝代"
        )
    ),
    FEE_BILL_HEADERS(
        listOf(
            "物品编码",
            "物品名称",
            "印次",
            "预估单号",
            "预估单行号",
            "费用项目",
            "往来单位编码",
            "往来单位",
            "业务日期",
            "记账日期",
            "业务部门编码",
            "业务部门",
            "业务员编码",
            "业务员",
            "自备材料金额",
            "预估金额(元)",
            "税率(%)",
            "暂估税金(元)",
            "结算单号",
            "结算单行号",
            "往来单位编码（结算）",
            "往来单位（结算）",
            "业务日期（结算）",
            "记账日期（结算）",
            "业务部门编码（结算）",
            "业务部门（结算）",
            "业务员编码（结算）",
            "业务员（结算）",
            "本次结算金额(元)",
            "税率(%)（结算）",
            "税金(元)",
            "增值税(元）",
            "城建税(元）",
            "教育费附加税(元）",
            "地方教育附加税(元)",
            "应纳税所得额(元）",
            "其他扣款金额(元）",
            "扣款原因",
            "发票类型",
            "发票号",
            "是否结算完成",
            "开户行",
            "开户行分行",
            "账户名称",
            "银行账号（结算）",
            "付款方开户银行（结算）",
            "付款方银行账号（结算）",
            "备注",
            "付款单号",
            "付款单行号",
            "业务日期（付款）",
            "记账日期（付款）",
            "业务部门编码（付款）",
            "业务部门（付款）",
            "业务员编码（付款）",
            "业务员（付款）",
            "本次支付金额(元)",
            "税金(元)（付款）"
        )
    ),
    OTHERS_SKIP_HEADERS(
        listOf(
            "用户属性",
            "查询权限"
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

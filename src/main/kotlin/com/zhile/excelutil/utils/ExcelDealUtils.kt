package com.zhile.excelutil.utils

import com.zhile.excelutil.service.ExcelHeaderData
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.slf4j.Logger
import org.springframework.stereotype.Component
import java.text.DecimalFormat
import java.text.SimpleDateFormat

/**
 * @author zlhp
 * @date 2025/6/25 15:03
 * @description:
 */
@Component
class ExcelDealUtils {
    private val logger: Logger = org.slf4j.LoggerFactory.getLogger(ExcelDealUtils::class.java)

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
            return ""
        }

        return when (cell.cellType) {
            CellType.STRING -> {
                val stringValue = cell.stringCellValue.trim()
                // 处理文本格式的数字（绿色小三角情况）
                handleTextAsNumber(stringValue)
            }

            CellType.NUMERIC -> formatNumericCell(cell)

            CellType.BOOLEAN -> cell.booleanCellValue.toString()

            CellType.FORMULA -> evaluateFormulaCell(cell, evaluator)

            CellType.BLANK -> ""

            CellType.ERROR -> "#ERROR!"

            else -> "未知类型"
        }
    }

    /**
     * 处理可能是数字但以文本形式存储的内容
     * 这是导致绿色小三角出现的主要原因
     */
    private fun handleTextAsNumber(value: String): String {
        if (value.isEmpty()) return value

        // 尝试识别并处理各种数字格式
        return when {
            // 1. 纯数字（整数或小数）
            isNumeric(value) -> {
                // 正则表达式匹配整数或浮点数
                // ^[-+]?        -> 匹配可选的正负号
                // (\\d+)       -> 匹配一个或多个数字
                // (\\.\\d+)?   -> 匹配可选的小数点和其后的一个或多个数字
                // $            -> 匹配字符串结束
                val numericRegex = "^[-+]?(\\d+(\\.\\d+)?|\\.\\d+)$".toRegex()

                if (value.matches(numericRegex)) {
                    value // 如果是数字字符串，直接返回原始字符串
                } else {
                    value // 如果不是数字字符串，也返回原始字符串
                }
            }

            // 2. 带千分位分隔符的数字 (如: "1,234.56")
            isNumberWithCommas(value) -> {
                val cleanNumber = value.replace(",", "")
                val numValue = cleanNumber.toDoubleOrNull()
                if (numValue != null) {
                    formatNumericValue(numValue)
                } else {
                    value
                }
            }

            // 3. 带货币符号的数字 (如: "$123.45", "¥100")
            isCurrencyNumber(value) -> {
                val cleanNumber = value.replace(Regex("[^\\d.-]"), "")
                val numValue = cleanNumber.toDoubleOrNull()
                if (numValue != null) {
                    formatNumericValue(numValue)
                } else {
                    value
                }
            }

            // 4. 百分比格式 (如: "50%")
            isPercentage(value) -> {
                val percentValue = value.replace("%", "").trim()
                val numValue = percentValue.toDoubleOrNull()
                if (numValue != null) {
                    // 保持百分比格式或转换为小数，根据需求决定
                    "${formatNumericValue(numValue)}%"
                } else {
                    value
                }
            }

            // 5. 科学计数法 (如: "1.23E+4")
            isScientificNotation(value) -> {
                val numValue = value.toDoubleOrNull()
                if (numValue != null) {
                    formatNumericValue(numValue)
                } else {
                    value
                }
            }

            // 6. 日期格式的文本
            isPossibleDateText(value) -> {
                parseDateFromText(value) ?: value
            }

            // 其他情况保持原样
            else -> value.take(1200)
        }
    }

    /**
     * 尝试从文本解析日期
     */
    private fun parseDateFromText(value: String): String? {
        val dateFormats = listOf(
            "yyyy-MM-dd",
            "yyyy/MM/dd",
            "dd-MM-yyyy",
            "dd/MM/yyyy",
            "MM-dd-yyyy",
            "MM/dd/yyyy"
        )

        for (pattern in dateFormats) {
            try {
                val formatter = SimpleDateFormat(pattern)
                formatter.isLenient = false
                val date = formatter.parse(value)
                // 返回标准格式的日期字符串
                return SimpleDateFormat("yyyy-MM-dd").format(date)
            } catch (e: Exception) {
                // 继续尝试下一个格式
            }
        }
        return null
    }

    /**
     * 检查是否为纯数字
     */
    private fun isNumeric(value: String): Boolean {
        return value.matches(Regex("^-?\\d*\\.?\\d+$"))
    }

    /**
     * 检查是否为带千分位分隔符的数字
     */
    private fun isNumberWithCommas(value: String): Boolean {
        return value.matches(Regex("^-?\\d{1,3}(,\\d{3})*(\\.\\d+)?$"))
    }

    /**
     * 检查是否为货币格式数字
     */
    private fun isCurrencyNumber(value: String): Boolean {
        // 匹配常见货币符号：$, ¥, €, £ 等
        return value.matches(Regex("^[\\$¥€£]?-?\\d+(\\.\\d{2})?[\\$¥€£]?$"))
    }

    /**
     * 检查是否为百分比格式
     */
    private fun isPercentage(value: String): Boolean {
        return value.matches(Regex("^-?\\d+(\\.\\d+)?%$"))
    }

    /**
     * 检查是否为科学计数法
     */
    private fun isScientificNotation(value: String): Boolean {
        return value.matches(Regex("^-?\\d+(\\.\\d+)?[eE][+-]?\\d+$"))
    }

    /**
     * 检查是否可能是日期文本
     */
    private fun isPossibleDateText(value: String): Boolean {
        // 匹配常见日期格式：yyyy-mm-dd, dd/mm/yyyy, mm-dd-yyyy 等
        return value.matches(Regex("^\\d{1,4}[-/]\\d{1,2}[-/]\\d{1,4}$")) ||
                value.matches(Regex("^\\d{1,2}[-/]\\d{1,2}[-/]\\d{2,4}$"))
    }

    /**
     * 格式化数字类型的单元格
     */
    private fun formatNumericCell(cell: Cell): String {
        return if (DateUtil.isCellDateFormatted(cell)) {
            // 日期格式化 - 可以根据需要自定义格式
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            dateFormat.format(cell.dateCellValue)
        } else {
            formatNumericValue(cell.numericCellValue)
        }
    }

    /**
     * 格式化数字值，整数去掉小数点
     */
    private fun formatNumericValue(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            // 如果数值等于其整数部分，说明是整数
            value.toLong().toString()
        } else {
            // 使用 DecimalFormat 避免科学计数法
            val decimalFormat = DecimalFormat("#.##########")
            decimalFormat.format(value)
        }
    }

    /**
     * 评估公式单元格
     */
    private fun evaluateFormulaCell(cell: Cell, evaluator: FormulaEvaluator): String {
        return try {
            val cellValue = evaluator.evaluate(cell)
            when (cellValue.cellType) {
                CellType.STRING -> cellValue.stringValue.trim()

                CellType.NUMERIC -> {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        dateFormat.format(DateUtil.getJavaDate(cellValue.numberValue))
                    } else {
                        formatNumericValue(cellValue.numberValue)
                    }
                }

                CellType.BOOLEAN -> cellValue.booleanValue.toString()

                CellType.ERROR -> "#ERROR!"

                else -> ""
            }
        } catch (e: Exception) {
            "#FORMULA_ERROR!"
        }
    }


    /**
     * 获取指定行的所有合并单元格区域
     */
    private fun getMergedRegionsForRow(sheet: Sheet, rowNum: Int): List<CellRangeAddress> {
        val mergedRegions = mutableListOf<CellRangeAddress>()
        for (i in 0 until sheet.numMergedRegions) {
            val mergedRegion = sheet.getMergedRegion(i)
            if (mergedRegion.firstRow <= rowNum && mergedRegion.lastRow >= rowNum) {
                mergedRegions.add(mergedRegion)
            }
        }
        return mergedRegions
    }

    /**
     * 获取单元格值，支持合并单元格
     */
    private fun getCellValueWithMergedSupport(
        cell: Cell?,
        evaluator: FormulaEvaluator,
        mergedRegions: List<CellRangeAddress>,
        colIndex: Int,
        rowIndex: Int
    ): String {
        // 首先尝试直接获取单元格值
        val directValue = getCellValue(cell, evaluator)
        if (directValue.isNotEmpty()) {
            return directValue
        }

        // 如果直接获取失败，检查是否在合并单元格中
        val mergedRegion = findMergedRegionContaining(mergedRegions, rowIndex, colIndex)
        if (mergedRegion != null) {
            // 获取合并单元格的主单元格（左上角单元格）
            val sheet = cell?.sheet
            val masterCell = sheet?.getRow(mergedRegion.firstRow)?.getCell(mergedRegion.firstColumn)
            return getCellValue(masterCell, evaluator)
        }

        return ""
    }

    /**
     * 获取单元格的值
     */
    private fun getCellValue(cell: Cell?, evaluator: FormulaEvaluator): String {
        return if (cell != null) {
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
    }

    /**
     * 查找包含指定位置的合并单元格区域
     */
    private fun findMergedRegionContaining(
        mergedRegions: List<CellRangeAddress>,
        rowNum: Int,
        colNum: Int
    ): CellRangeAddress? {
        return mergedRegions.find { region ->
            region.firstRow <= rowNum && region.lastRow >= rowNum &&
                    region.firstColumn <= colNum && region.lastColumn >= colNum
        }
    }

    /**
     * 检查给定的 Excel 行是否包含 ExcelHeaderData 枚举中定义的必要属性。
     * 不要求完全匹配或顺序一致，支持模糊匹配（忽略大小写、空格、常见标点符号）。
     *
     * @param row 从 Excel 表格中获取的当前行对象。
     * @param evaluator 用于评估公式的 FormulaEvaluator 实例。
     * @return 如果行包含任何一个预定义头部列表的所有必要属性，则返回对应的ExcelHeaderData和列索引映射；否则返回null。
     */
    fun isValidExcelHeaderRow(
        row: Row, evaluator: FormulaEvaluator
    ): Pair<ExcelHeaderData?, Map<String, Int>>? {
        val rowData = mutableListOf<String>()
        val headerIndexMap = mutableMapOf<String, Int>() // 存储标准列名与索引的映射
        val normalizedHeaderMap = mutableMapOf<String, String>() // 存储标准列名与实际列名的映射
        val sheet = row.sheet

        // 获取当前行的所有合并单元格区域
        val mergedRegions = getMergedRegionsForRow(sheet, row.rowNum)

        // 获取行中的所有列名，包括合并单元格
        for (i in 0 until row.lastCellNum) {
            val cell = row.getCell(i)
            val cellValue = getCellValueWithMergedSupport(cell, evaluator, mergedRegions, i, row.rowNum)

            if (cellValue.isNotEmpty()) {
                rowData.add(cellValue)
                // 存储原始列名和索引
                headerIndexMap[cellValue] = i

                // 如果是合并单元格，需要为合并区域内的所有列都映射到相同的值
                val mergedRegion = findMergedRegionContaining(mergedRegions, row.rowNum, i)
                if (mergedRegion != null) {
                    // 为合并单元格区域内的所有列建立映射
                    for (colIndex in mergedRegion.firstColumn..mergedRegion.lastColumn) {
                        headerIndexMap[cellValue] = colIndex
                    }
                }
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
                    normalizeString(actual).contains(normalizeString(required)) ||
                            normalizeString(required).contains(normalizeString(actual))
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
                val standardizedHeaderIndexMap = createStandardizedHeaderIndexMap(
                    matchedHeaders, headerIndexMap, mergedRegions, row.rowNum
                )
                return Pair(headerEnum, standardizedHeaderIndexMap)
            }
        }
        return null
    }

    /**
     * 创建标准化的表头索引映射，考虑合并单元格
     */
    private fun createStandardizedHeaderIndexMap(
        matchedHeaders: Map<String, String>,
        headerIndexMap: Map<String, Int>,
        mergedRegions: List<CellRangeAddress>,
        rowNum: Int
    ): Map<String, Int> {
        val standardizedMap = mutableMapOf<String, Int>()

        matchedHeaders.forEach { (standardHeader, actualHeader) ->
            val baseIndex = headerIndexMap[actualHeader] ?: -1
            if (baseIndex != -1) {
                // 检查是否在合并单元格中
                val mergedRegion = findMergedRegionContaining(mergedRegions, rowNum, baseIndex)
                if (mergedRegion != null) {
                    // 对于合并单元格，可以选择使用第一列或最后一列的索引
                    // 这里选择使用第一列的索引
                    standardizedMap[standardHeader] = mergedRegion.firstColumn
                } else {
                    standardizedMap[standardHeader] = baseIndex
                }
            }
        }

        return standardizedMap
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
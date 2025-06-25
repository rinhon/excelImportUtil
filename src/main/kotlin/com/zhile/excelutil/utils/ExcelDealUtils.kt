package com.zhile.excelutil.utils

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.FormulaEvaluator
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

            else -> ""
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
                val numValue = value.toDoubleOrNull()
                if (numValue != null) {
                    formatNumericValue(numValue)
                } else {
                    value
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


}
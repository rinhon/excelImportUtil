package com.zhile.excelutil.service

import com.zhile.excelutil.dao.ImImportResultRepository
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * @author Rinhon
 * @date 2025/6/16
 * @description: Excel下载服务
 */
@Service
class ExcelDownloadService(private val importResultRepository: ImImportResultRepository) {

    /**
     * 生成错误数据Excel
     * @return ByteArrayInputStream Excel文件的输入流
     */
    fun generateErrorDataExcel(): ByteArrayInputStream {
        // 获取所有导入错误数据
        val errorDataList = importResultRepository.findAll()

        // 创建Excel工作簿
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("导入错误数据")

        // 创建标题行样式
        val headerStyle = workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.LIGHT_BLUE.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
        }

        // 创建标题行
        val headerRow = sheet.createRow(0)
        val headers = arrayOf("检查类型", "错误信息", "错误位置")

        headers.forEachIndexed { index, header ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(header)
            cell.cellStyle = headerStyle
        }

        // 填充数据行
        errorDataList.forEachIndexed { index, errorData ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(errorData.checkType ?: "")
            row.createCell(1).setCellValue(errorData.errorInfo ?: "")
            row.createCell(2).setCellValue(errorData.errorLocation ?: "")
        }

        // 自动调整列宽
        for (i in headers.indices) {
            sheet.autoSizeColumn(i)
        }

        // 将工作簿写入字节数组
        val outputStream = ByteArrayOutputStream()
        workbook.write(outputStream)
        workbook.close()

        return ByteArrayInputStream(outputStream.toByteArray())
    }
}

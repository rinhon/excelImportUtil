package com.zhile.excelutil.controller

import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.FileNotFoundException

/**
 * @author Rinhon
 * @date 2025/6/16 19:14
 * @description: 模板下载控制器
 */
@RestController
@RequestMapping("/api/excel")
class TemplateDownloadController {

    @GetMapping("/downloadTemplate")
    fun downloadTemplate(): ResponseEntity<InputStreamResource> {
        val templateFileName = "template.xlsx"
        val templatePath = "classpath:mytemplates/$templateFileName" // 注意这里的路径

        return try {
            // 1. 获取文件资源
            // ResourceUtils.getFile 可以从 classpath 获取文件，但它会尝试将资源转换为 File 对象
            // 对于打包的 JAR，直接使用 ClassLoader 获取 InputStream 更可靠
            val inputStream = javaClass.classLoader.getResourceAsStream("mytemplates/$templateFileName")

            if (inputStream == null) {
                // 如果文件不存在或无法访问，返回 404
                ResponseEntity.notFound().build()
            } else {
                val resource = InputStreamResource(inputStream)

                // 2. 设置 HTTP 响应头，告诉浏览器这是文件下载
                val headers = HttpHeaders().apply {
                    // 设置 Content-Disposition 使得浏览器下载文件而不是直接打开
                    // "attachment" 表示作为附件下载
                    // "filename" 是用户下载时看到的文件名，可以修改
                    set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$templateFileName\"")
                    // 设置 Content-Type 为 Excel 文件的 MIME 类型
                    contentType =
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                }

                // 3. 构建并返回 ResponseEntity
                ResponseEntity.ok()
                    .headers(headers)
                    .body(resource)
            }
        } catch (e: FileNotFoundException) {
            // 如果文件未找到，返回 404
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            // 捕获其他潜在异常，返回 500 错误
            e.printStackTrace() // 打印堆栈跟踪以便调试
            ResponseEntity.internalServerError().build()
        }
    }
}


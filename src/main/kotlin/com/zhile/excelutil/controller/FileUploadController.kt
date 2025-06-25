package com.zhile.excelutil.controller

import com.zhile.excelutil.handler.FileProcessingWebSocketHandler
import com.zhile.excelutil.service.FileProcessingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*


/***
 * @author Rinhon
 * @date 2025/6/17 09:08
 * @description:  excel文件导入
 */
@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping(name = "excel文件导入", value = ["/fileUpload"])
class FileUploadController(
    private val fileProcessingService: FileProcessingService,
    private val webSocketHandler: FileProcessingWebSocketHandler
) {

    /**
     * @author Rinhon
     * @date 2020/12/17 16:58
     * @description 文件上传
     */
    @PostMapping("/multipleFiles")
    fun uploadMultipleFiles(
        @RequestParam("files") files: Array<MultipartFile>, @RequestHeader("Session-Id") sessionId: String
    ): ResponseEntity<Map<String, Any>> {
        return try {
            if (files.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    mapOf(
                        "success" to false, "message" to "没有选择文件"
                    )
                )
            }
            // 验证文件类型
            val allowedExtensions = listOf("xls", "xlsx")
            val invalidFiles = files.filter { file ->
                // 验证文件类型
                val extension = file.originalFilename
                    ?.substringAfterLast('.', "")
                    ?.lowercase()
                extension !in allowedExtensions
            }

            if (invalidFiles.isNotEmpty()) {
                return ResponseEntity.badRequest().body(
                    mapOf(
                        "success" to false, "message" to "文件格式不支持，只支持 .xls 和 .xlsx 格式"
                    )
                )
            }

            // 创建处理任务
            val tasks = files.map { file ->
                val taskId = UUID.randomUUID().toString()
                mapOf(
                    "taskId" to taskId, "fileName" to (file.originalFilename ?: "未知文件名")
                )
            }

            //获取当前请求的sessionID
            println("session id = (${sessionId})")
            val organId: Long = 212;
            // 异步处理文件
            fileProcessingService.processFilesAsync(files.toList(), tasks, sessionId, organId)

            ResponseEntity.ok(
                mapOf(
                    "success" to true, "message" to "文件上传成功", "tasks" to tasks
                )
            )

        } catch (e: Exception) {
            println(e.message)
            ResponseEntity.internalServerError().body(
                mapOf(
                    "success" to false, "message" to "上传失败: ${e.message}"
                )
            )
        }
    }

}
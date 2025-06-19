package com.zhile.excelutil.utils

import org.slf4j.LoggerFactory
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

class FileUtils(private val tempStoragePath: String) {

    private val logger = LoggerFactory.getLogger(FileUtils::class.java)

    private val temporaryDirectory: Path = Paths.get(tempStoragePath).toAbsolutePath().normalize()

    init {
        // 确保临时存储目录存在
        try {
            Files.createDirectories(temporaryDirectory)
            logger.info("临时存储目录已准备好: $temporaryDirectory")
        } catch (e: IOException) {
            logger.error("无法创建临时存储目录: $temporaryDirectory", e)
            throw RuntimeException("初始化文件存储服务失败：无法创建临时目录。", e)
        }
    }

    /**
     * 清空指定临时存储目录下的所有文件和子目录。
     * 请谨慎使用此方法，确保此目录下没有其他重要文件。
     */
    fun cleanTemporaryStorage() {
        logger.info("开始清空临时存储目录: $temporaryDirectory")
        try {
            // 遍历目录下的所有文件和子目录
            Files.walk(temporaryDirectory)
                .sorted(Comparator.reverseOrder()) // 先删除子文件，再删除子目录，最后目录本身
                .forEach { path ->
                    if (path != temporaryDirectory) { // 避免删除根目录本身
                        try {
                            Files.delete(path)
                            logger.debug("已删除临时文件/目录: {}", path)
                        } catch (e: IOException) {
                            logger.warn("无法删除临时文件/目录: $path", e)
                        }
                    }
                }
            logger.info("临时存储目录清空完成: $temporaryDirectory")
        } catch (e: IOException) {
            logger.error("清空临时存储目录失败: $temporaryDirectory", e)

        }
    }

    /**
     * 将 MultipartFile 持久化存储到临时目录，并在保存前清空该目录。
     *
     * @param multipartFile 要持久化的 MultipartFile 对象。
     * @return 持久化后的文件对象。
     * @throws IOException 如果文件保存失败。
     */
    fun saveMultipartFile(multipartFile: MultipartFile): Path {


        if (multipartFile.isEmpty) {
            throw IOException("上传文件为空。")
        }

        // 获取原始文件名（可能包含路径，但我们只取文件名部分）
        val originalFileName = multipartFile.originalFilename?.let {
            Paths.get(it).fileName.toString()
        } ?: "unknown_file"

        // 生成一个唯一的文件名以避免冲突
        val uniqueFileName = "${UUID.randomUUID()}_$originalFileName"
        val destinationPath = temporaryDirectory.resolve(uniqueFileName)

        logger.info("开始保存文件: $originalFileName 到临时路径: $destinationPath")

        try {
            // 使用 try-use 自动关闭 InputStream
            multipartFile.inputStream.use { inputStream ->
                Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING)
            }
            logger.info("文件保存成功: $destinationPath")
            return destinationPath
        } catch (e: IOException) {
            logger.error("保存文件失败: $originalFileName 到 $destinationPath", e)
            throw IOException("无法保存文件: $originalFileName", e)
        }
    }

    // 可以添加一个方法用于在处理完成后删除特定文件
    fun deleteFile(filePath: Path) {
        try {
            Files.deleteIfExists(filePath)
            logger.info("已删除文件: $filePath")
        } catch (e: IOException) {
            logger.warn("无法删除文件: $filePath", e)
        }
    }


    /**
     * 获取临时存储目录中所有持久化的文件。
     *
     * @return 存储在临时目录中的所有文件路径列表。
     */
    fun listAllStoredFiles(): List<Path> {
        val files = mutableListOf<Path>()
        try {
            if (!Files.exists(temporaryDirectory) || !Files.isDirectory(temporaryDirectory)) {
                logger.warn("临时存储目录不存在或不是目录: $temporaryDirectory")
                return emptyList()
            }
            Files.list(temporaryDirectory).use { stream -> // 只列出第一级子项，不递归
                stream.filter { Files.isRegularFile(it) } // 过滤出常规文件
                    .forEach { files.add(it) }
            }
            logger.info("已获取临时存储目录中的 ${files.size} 个文件。")
        } catch (e: IOException) {
            logger.error("获取临时存储目录文件列表失败: $temporaryDirectory", e)
        }
        return files
    }
}
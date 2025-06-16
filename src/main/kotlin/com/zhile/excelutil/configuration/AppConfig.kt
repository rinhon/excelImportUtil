package com.zhile.excelutil.configuration

import com.zhile.excelutil.utils.FileUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {

    @Value("\${app.temp.upload.dir:/tmp/excel_uploads}") // 从配置文件读取路径，默认值为 /tmp/excel_uploads
    private lateinit var uploadDir: String

    @Bean
    fun fileStorageService(): FileUtils {
        return FileUtils(uploadDir)
    }
}
package com.zhile.excelutil.configuration

import com.zhile.excelutil.handler.FileProcessingWebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

/**
 * @Author: Rinhon
 * @Date: 2023/4/23 16:25
 * @description:webSocket配置
 */
@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val fileProcessingHandler: FileProcessingWebSocketHandler
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(fileProcessingHandler, "/ws")
            .setAllowedOrigins("*")
    }
}
package com.zhile.excelutil.handler

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.*
import java.security.Principal
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListSet

@Component
class FileProcessingWebSocketHandler : WebSocketHandler {

    private val logger = LoggerFactory.getLogger(FileProcessingWebSocketHandler::class.java)
    private val sessions = ConcurrentHashMap<String, WebSocketSession>()
    private val objectMapper = ObjectMapper()

    // 修正：使用一对多映射，存储用户名到其所有活跃 WebSocket Session ID 的集合
    private val userSessionsMap = ConcurrentHashMap<String, ConcurrentSkipListSet<String>>()


    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions[session.id] = session
        logger.info("WebSocket连接已建立: ${session.id}")

        var principal: Principal? = session.principal
        val usernameToClient: String // 准备发送给客户端的用户名

        if (principal == null) {
            val anonymousUsername = "anonymous-${session.id}"

            logger.warn("未认证的 WebSocket 连接建立，已创建匿名 Principal: $anonymousUsername")
            usernameToClient = anonymousUsername // 匿名用户的ID
        } else {
            usernameToClient = principal.name // 已认证用户的用户名
        }

        if (principal != null) {
            principal.name?.let { username ->
                userSessionsMap.computeIfAbsent(username) { ConcurrentSkipListSet() }.add(session.id)
                logger.info("用户 '$username' 的 WebSocket 连接已建立，Session ID: ${session.id}，当前活跃会话数：${userSessionsMap[username]?.size}")
            } ?: run {
                logger.error("在 afterConnectionEstablished 中无法获取或创建 Principal 的用户名，Session ID: ${session.id}")
                // 如果确实无法获取用户名，这里可以关闭会话或者发送错误消息
                // session.close(CloseStatus.SERVER_ERROR.withReason("Failed to identify user"));
                return
            }
        }

        // **新增部分：向客户端发送身份信息**
        try {
            val identityMessage = mapOf(
                "type" to "identity",
                "username" to usernameToClient,
                "sessionId" to session.id // 也可以把当前的 WebSocket Session ID 发给客户端，如果客户端需要
            )
            session.sendMessage(TextMessage(objectMapper.writeValueAsString(identityMessage)))
            logger.info("已向客户端 ${session.id} 发送身份信息: $usernameToClient")
        } catch (e: Exception) {
            logger.error("发送身份信息到客户端失败: ${session.id}", e)
        }
    }
// ... 其他方法

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
        logger.info("收到消息: ${message.payload}")
        // 可以处理客户端发送的消息，比如取消任务等
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        logger.error("WebSocket传输错误: ${session.id}", exception)
        // 传输错误时也应清理会话
        cleanupSession(session.id)
    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        sessions.remove(session.id)
        logger.info("WebSocket连接已关闭: ${session.id}")
        // 连接关闭时清理会话关联
        cleanupSession(session.id)
    }

    override fun supportsPartialMessages(): Boolean = false

    // 广播进度更新到所有连接的客户端
    fun broadcastProgress(data: ProgressData) {
        val message = objectMapper.writeValueAsString(data)
        val textMessage = TextMessage(message)

        sessions.values.removeIf { session ->
            try {
                if (session.isOpen) {
                    session.sendMessage(textMessage)
                    false // 成功发送，不移除
                } else {
                    true // 会话已关闭，移除
                }
            } catch (e: Exception) {
                logger.error("发送消息失败: ${session.id}", e)
                true // 发生异常，移除
            }
        }
        // 当广播时发现会话关闭或发送失败，也应该从 userSessionsMap 中清理
        // 考虑到效率，这里不直接清理 userSessionsMap，而是依赖 sendToUser 或 cleanupSession
    }

    // 向特定会话发送消息
    fun sendToSession(sessionId: String, data: ProgressData) {
        sessions[sessionId]?.let { session ->
            try {
                if (session.isOpen) {
                    val message = objectMapper.writeValueAsString(data)
                    session.sendMessage(TextMessage(message))
                } else {
                    logger.info("会话连接断开：$sessionId")
                    cleanupSession(sessionId) // 会话断开，清理
                }
            } catch (e: Exception) {
                logger.error("发送消息到会话失败: $sessionId", e)
                cleanupSession(sessionId) // 发送失败，清理
            }
        }
    }

    /**
     * 新增：向用户的所有活跃会话发送消息
     * 这是 Controller 通常会调用的方法，因为它通过 username 来定位所有会话
     */
    fun sendToUser(username: String, data: ProgressData) {
        val message = objectMapper.writeValueAsString(data)
        val textMessage = TextMessage(message)

        userSessionsMap[username]?.let { sessionIds ->
            sessionIds.removeIf { sessionId -> // 遍历并移除无效会话
                val session = sessions[sessionId]
                if (session != null && session.isOpen) {
                    try {
                        session.sendMessage(textMessage)
                        false // 成功发送，保留 sessionId
                    } catch (e: Exception) {
                        logger.error("发送消息到用户 '$username' 的会话 '$sessionId' 失败", e)
                        cleanupSession(sessionId) // 发送失败，清理
                        true // 移除此 sessionId
                    }
                } else {
                    logger.info("用户 '$username' 的会话 '$sessionId' 已关闭或不存在，将移除。")
                    cleanupSession(sessionId) // 会话关闭，清理
                    true // 移除此 sessionId
                }
            }
            if (sessionIds.isEmpty()) { // 如果用户的所有会话都已移除，也移除用户条目
                userSessionsMap.remove(username)
            }
        } ?: logger.info("用户 '$username' 没有活跃的 WebSocket 会话。")
    }


    /**
     * 获取指定用户的所有活跃 WebSocket Session ID。
     * Controller 可以调用此方法来获取会话列表，但通常直接调用 sendToUser 更方便。
     */
    fun getSessionIdsForUser(username: String): Set<String>? {
        return userSessionsMap[username]
    }

    /**
     * （可选）获取指定用户的任意一个活跃 WebSocket Session ID。
     * 如果你的业务逻辑只需要向该用户发送一条消息，且不关心具体是哪个会话，可以使用。
     */
    fun getOneSessionIdForUser(username: String): String? {
        return userSessionsMap[username]?.firstOrNull()
    }


    /**
     * 辅助方法：统一清理会话（从全局 sessions 和用户会话映射中移除）
     */
    private fun cleanupSession(sessionId: String) {
        sessions.remove(sessionId)
        // 从所有用户的会话集合中移除此 sessionId
        userSessionsMap.forEach { (username, sessionIds) -> // <--- CHANGE IS HERE: name the key as 'username'
            sessionIds.remove(sessionId)
            // 如果某个用户的会话集合为空了，就移除该用户
            if (sessionIds.isEmpty()) {
                userSessionsMap.remove(username) // <--- Use the named key 'username'
            }
        }
    }
}

data class ProgressData(
    val type: String, // "processing", "completed", "error", "cancelled"
    val taskId: String,
    val fileName: String? = null,
    val progress: Int = 0,
    val status: String, // "waiting", "processing", "completed", "error"
    val message: String? = null
)
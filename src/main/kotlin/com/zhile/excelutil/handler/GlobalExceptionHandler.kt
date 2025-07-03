package com.zhile.excelutil.handler

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

// @RestControllerAdvice 结合了 @ControllerAdvice 和 @ResponseBody
// 它使得这个类能够处理整个应用程序中的异常，并直接返回 JSON/XML 响应
@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /**
     * 捕获所有未被特定处理的其他异常 (兜底异常)
     */
    @ExceptionHandler(Exception::class)
    fun handleAllUncaughtException(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error("捕获到未处理的异常: ${ex.message}", ex) // 记录完整堆栈
        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Internal Server Error",
            message = "服务器内部错误，请稍后重试。" // 对用户友好的通用消息
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    /**
     * 捕获特定类型的异常：例如 IllegalArgumentException
     * 当方法参数不合法或业务逻辑抛出非法参数异常时
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        logger.warn("捕获到非法参数异常: ${ex.message}")
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            message = ex.message ?: "请求参数不合法。" // 使用异常的 message
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    /**
     * 捕获 MissingServletRequestParameterException
     * 当必需的 @RequestParam 参数缺失时
     */
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParameterException(ex: MissingServletRequestParameterException): ResponseEntity<ErrorResponse> {
        logger.warn("捕获到请求参数缺失异常: ${ex.parameterName} 是必需的")
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            message = "请求参数 '${ex.parameterName}' 缺失。"
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    /**
     * 捕获 MethodArgumentTypeMismatchException
     * 当方法参数类型不匹配时 (例如，期望 Integer 却传入了字符串)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatchException(ex: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> {
        logger.warn("捕获到参数类型不匹配异常: ${ex.name} 类型错误，期望 ${ex.requiredType?.simpleName}")
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            message = "参数 '${ex.name}' 类型错误，期望类型为 ${ex.requiredType?.simpleName}。"
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    /**
     * 捕获 MethodArgumentNotValidException (通常用于 @RequestBody 的校验失败)
     * 当使用 @Valid 或 @Validated 进行对象参数校验失败时
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.joinToString(", ") { error ->
            "${error.field}: ${error.defaultMessage}"
        }

        logger.warn("捕获到参数校验失败异常: $errors")
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Validation Error",
            message = "请求参数校验失败: $errors"
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }


    /**
     * 定义一个标准的错误响应数据类，用于返回给客户端
     */
    data class ErrorResponse(
        val status: Int,
        val error: String,
        val message: String
    )
}

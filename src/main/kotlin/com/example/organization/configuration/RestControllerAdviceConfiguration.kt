package com.example.organization.configuration

import com.example.organization.data.common.StandardResponse
import com.example.organization.exception.CustomException
import mu.KotlinLogging
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RestControllerAdviceConfiguration {

    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(value = [CustomException::class])
    fun handleCustomException(customException: CustomException): StandardResponse<*> {
        logger.error(customException) { "$customException occured" }
        return StandardResponse(false, customException.message, null)
    }

    @ExceptionHandler(value = [Exception::class])
    fun handleException(exception: Exception): StandardResponse<*>{
        logger.error(exception) { "$exception occured" }
        return StandardResponse(false, "요청 처리과정중에 오류가 발생했습니다.", null)
    }
}
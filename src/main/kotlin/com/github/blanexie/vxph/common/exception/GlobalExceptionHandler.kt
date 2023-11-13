package com.github.blanexie.vxph.common.exception

import com.github.blanexie.vxph.common.Result
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(VxphException::class)
    fun handleVxphException(e: VxphException): Result {
        log.error("全局异常拦截了", e)
        return Result.fail(e.sysCode, e.message!!)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(e: Exception): Result {
        log.error("全局异常拦截了", e)
        return Result.fail(SysCode.ServerError)
    }

}
package com.github.blanexie.vxph.common

import cn.dev33.satoken.util.SaResult
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(VxphException::class)
    fun handleVxphException(e: VxphException): SaResult {
        log.error("全局异常拦截了", e)
        return SaResult.get(e.sysCode.code, e.message!!, null)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(e: Exception): SaResult {
        log.error("全局异常拦截了", e)
        return SaResult.error().fail(SysCode.ServerError)
    }

}
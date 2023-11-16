package com.github.blanexie.vxph.common.exception

import cn.dev33.satoken.exception.NotLoginException
import com.github.blanexie.vxph.common.web.WebResp
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody


@ControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    @ResponseBody
    @ExceptionHandler(VxphException::class)
    fun handleVxphException(e: VxphException): WebResp {
        log.error("全局Vxph异常拦截了", e)
        return WebResp.fail(e.sysCode, e.message!!)
    }

    @ResponseBody
    @ExceptionHandler(NotLoginException::class)
    fun handleNotLoginException(e: NotLoginException): WebResp {
        log.error("全局NotLogin异常拦截了, {}", e.message)
        return WebResp.fail(SysCode.NotLoginError)
    }

    @ResponseBody
    @ExceptionHandler(Exception::class)
    fun handleGeneralException(e: Exception): WebResp {
        log.error("全局通用异常拦截了", e)
        return WebResp.fail(SysCode.ServerError)
    }

}
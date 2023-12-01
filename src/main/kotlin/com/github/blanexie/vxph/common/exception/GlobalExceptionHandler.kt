package com.github.blanexie.vxph.common.exception

import cn.dev33.satoken.exception.NotLoginException
import cn.dev33.satoken.exception.NotPermissionException
import cn.dev33.satoken.spring.SpringMVCUtil
import com.github.blanexie.vxph.common.entity.WebResp
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
        val request = SpringMVCUtil.getRequest()
        log.error("全局Vxph异常拦截了 {}", request.requestURI, e)
        return WebResp.fail(e.sysCode, e.message!!)
    }

    @ResponseBody
    @ExceptionHandler(NotPermissionException::class)
    fun handleNotPermissionException(e: NotPermissionException): WebResp {
        val request = SpringMVCUtil.getRequest()
        log.error("全局Vxph异常拦截了 {}",request.requestURI, e)
        return WebResp.fail(SysCode.PermissionNotAllow)
    }

    @ResponseBody
    @ExceptionHandler(NotLoginException::class)
    fun handleNotLoginException(e: NotLoginException): WebResp {
        val request = SpringMVCUtil.getRequest()
        log.error("全局NotLogin异常拦截了,pathInfo:{}  {}", request.requestURI, e.message)
        return WebResp.fail(SysCode.NotLoginError)
    }

    @ResponseBody
    @ExceptionHandler(Exception::class)
    fun handleGeneralException(e: Exception): WebResp {
        val request = SpringMVCUtil.getRequest()
        log.error("全局通用异常拦截了 {}",request.requestURI, e)
        return WebResp.fail(SysCode.ServerError)
    }

}
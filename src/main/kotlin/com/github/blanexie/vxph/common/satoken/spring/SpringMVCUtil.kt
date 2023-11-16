package com.github.blanexie.vxph.common.satoken.spring

import cn.dev33.satoken.error.SaSpringBootErrorCode
import cn.dev33.satoken.exception.NotWebContextException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

class SpringMVCUtil {

    companion object {
        /**
         * 获取当前会话的 request 对象
         * @return request
         */
        fun getRequest(): HttpServletRequest {
            val servletRequestAttributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?
                ?: throw NotWebContextException("非 web 上下文无法获取 HttpServletRequest").setCode(SaSpringBootErrorCode.CODE_20101)
            return servletRequestAttributes.request
        }

        /**
         * 获取当前会话的 response 对象
         * @return response
         */
        fun getResponse(): HttpServletResponse {
            val servletRequestAttributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?
                ?: throw NotWebContextException("非 web 上下文无法获取 HttpServletResponse").setCode(SaSpringBootErrorCode.CODE_20101)
            return servletRequestAttributes.response!!
        }

        /**
         * 判断当前是否处于 Web 上下文中
         * @return /
         */
        fun isWeb(): Boolean {
            return RequestContextHolder.getRequestAttributes() != null
        }
    }
}
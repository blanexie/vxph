package com.github.blanexie.vxph.common.satoken.servlet

import cn.dev33.satoken.context.model.SaResponse
import cn.dev33.satoken.exception.SaTokenException
import jakarta.servlet.http.HttpServletResponse

class SaResponseForServlet(val response: HttpServletResponse): SaResponse {
    override fun getSource(): Any {
        return response
    }

    override fun setStatus(sc: Int): SaResponse {
        response.status = sc
        return this
    }

    override fun setHeader(name: String?, value: String?): SaResponse {
        response.setHeader(name, value)
        return this
    }

    override fun addHeader(name: String?, value: String?): SaResponse {
        response.addHeader(name, value)
        return this
    }

    override fun redirect(url: String?): Any? {
        try {
            response.sendRedirect(url)
        } catch (e: Exception) {
            throw SaTokenException(e).setCode(SaServletErrorCode.CODE_20002)
        }
        return null
    }
}
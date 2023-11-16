package com.github.blanexie.vxph.common.satoken

import cn.dev33.satoken.SaManager
import cn.dev33.satoken.application.ApplicationInfo
import cn.dev33.satoken.context.model.SaRequest
import cn.dev33.satoken.exception.SaTokenException
import cn.dev33.satoken.servlet.error.SaServletErrorCode
import cn.dev33.satoken.util.SaFoxUtil
import jakarta.servlet.ServletException
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.io.IOException

class SaRequestForServlet(val request: HttpServletRequest) : SaRequest {
    override fun getSource(): Any {
        return request;
    }

    override fun getParam(name: String?): String? {
        return request.getParameter(name)
    }

    override fun getParamNames(): MutableList<String> {
        val parameterNames = request.parameterNames
        val list: MutableList<String> = ArrayList()
        while (parameterNames.hasMoreElements()) {
            list.add(parameterNames.nextElement())
        }
        return list
    }

    override fun getParamMap(): MutableMap<String, String> {
        // 获取所有参数
        val parameterMap = request.parameterMap
        val map: MutableMap<String, String> = LinkedHashMap(parameterMap.size)
        for (key in parameterMap.keys) {
            val values = parameterMap[key]!!
            map[key] = values[0]
        }
        return map
    }

    override fun getHeader(name: String): String? {
        return request.getHeader(name)
    }

    override fun getCookieValue(name: String): String? {
        val cookies: Array<Cookie>? = request.cookies
        if (cookies != null) {
            for (cookie in cookies) {
                if (name == cookie.name) {
                    return cookie.value
                }
            }
        }
        return null
    }

    override fun getRequestPath(): String {
        return ApplicationInfo.cutPathPrefix(request.requestURI)
    }

    override fun getUrl(): String {
        val currDomain = SaManager.getConfig().currDomain
        return if (!SaFoxUtil.isEmpty(currDomain)) {
            currDomain + this.requestPath
        } else request.requestURL.toString()
    }

    override fun getMethod(): String {
        return request.method
    }

    override fun forward(path: String): Any? {
        return try {
            val response: HttpServletResponse =
                SaManager.getSaTokenContextOrSecond().response.source as HttpServletResponse
            request.getRequestDispatcher(path).forward(request, response)
            null
        } catch (e: ServletException) {
            throw SaTokenException(e).setCode(SaServletErrorCode.CODE_20001)
        } catch (e: IOException) {
            throw SaTokenException(e).setCode(SaServletErrorCode.CODE_20001)
        }
    }
}
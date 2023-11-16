package com.github.blanexie.vxph.common.web

import com.github.blanexie.vxph.torrent.parseInfoHash
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver


class InfoHashParamResolver : AbstractNamedValueMethodArgumentResolver() {


    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(InfoHashParam::class.java)
    }

    override fun createNamedValueInfo(parameter: MethodParameter): NamedValueInfo {
        val ann = parameter.getParameterAnnotation(InfoHashParam::class.java)
        return NamedValueInfo(ann!!.name, true, null)
    }


    override fun resolveName(name: String, parameter: MethodParameter, request: NativeWebRequest): Any? {
        val servletRequest = request.getNativeRequest(HttpServletRequest::class.java)!!
        val queryString = servletRequest.queryString
        val split = queryString.split("&")
        val toList = split.filter { it.startsWith("$name=") }.toList()
        val args = toList.map {
            it.removePrefix("$name=")
        }.map { parseInfoHash(it) }.toList()
        if (args.size == 1) {
            return args[0]
        } else {
            return args
        }
    }


}
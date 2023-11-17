package com.github.blanexie.vxph.common.web

import com.github.blanexie.vxph.torrent.util.parseInfoHash
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


    fun parseInfoHash(encoded: String): String {
        return try {
            val r = StringBuilder()
            var i = 0
            while (i < encoded.length) {
                val c = encoded[i]
                if (c == '%') {
                    r.append(encoded[i + 1])
                    r.append(encoded[i + 2])
                    i += 2
                } else {
                    r.append(String.format("%02x", c.code))
                }
                i++
            }
            r.toString().lowercase()
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to decode info_hash: $encoded")
        }
    }

}
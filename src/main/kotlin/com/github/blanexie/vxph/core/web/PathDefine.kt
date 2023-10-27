package com.github.blanexie.vxph.core.web

import cn.hutool.core.lang.Singleton
import cn.hutool.core.util.ReflectUtil
import com.github.blanexie.vxph.core.contextMap
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import java.lang.reflect.Method


data class PathDefine(
    val clazz: Class<*>,
    val method: Method,
    val path: String,
    val reqMethod: String
) {
     fun newInstance(): Any {
        return contextMap.computeIfAbsent(clazz.name) {
            ReflectUtil.newInstance(clazz)
        }
    }

    fun invoke(request: HttpServerRequest): HttpServerResponse {
        return ReflectUtil.invoke(newInstance(), method, request)
    }

}



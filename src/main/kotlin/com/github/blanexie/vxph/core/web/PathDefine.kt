package com.github.blanexie.vxph.core.web

import cn.hutool.core.lang.Singleton
import cn.hutool.core.util.ReflectUtil
import com.github.blanexie.vxph.core.contextMap
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import java.lang.reflect.Method


data class PathDefine(
    val method: Method,
    val path: String,
    val reqMethod: String
)



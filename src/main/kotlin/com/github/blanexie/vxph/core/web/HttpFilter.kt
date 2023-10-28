package com.github.blanexie.vxph.core.web

import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse

interface HttpFilter {

    fun sort(): Int {
        return 0
    }

    fun before(request: HttpServerRequest): Boolean

    fun exception(request: HttpServerRequest, response: HttpServerResponse, e: Throwable): Boolean

}
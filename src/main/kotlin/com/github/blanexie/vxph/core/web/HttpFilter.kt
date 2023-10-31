package com.github.blanexie.vxph.core.web

import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.RoutingContext

interface HttpFilter {

    fun sort(): Int {
        return 0
    }

    fun before(ctx: RoutingContext): Boolean

    fun exception(ctx: RoutingContext, e: Throwable): Boolean

}
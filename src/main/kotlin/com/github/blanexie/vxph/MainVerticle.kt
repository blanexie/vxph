package com.github.blanexie.vxph

import com.github.blanexie.vxph.core.AbstractVerticle
import com.github.blanexie.vxph.core.PluginLoadVerticle
import com.github.blanexie.vxph.plugin.pluginFactory
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.awaitEvent
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture

/**
 * 启动三件事
 * 1. 加载框架verticle
 * 2. 加载插件
 * 3. 加载flow
 */
class MainVerticle : AbstractVerticle("mainVerticle", "0", "0") {

    private val log = LoggerFactory.getLogger(this::class.java)

    override suspend fun handleEnd() {
        val coreJdbcPlugin = vertx.deployVerticle(pluginFactory.buildCoreJdbcPlugin())
        coreJdbcPlugin.await()
        val plugins = vertx.deployVerticle(PluginLoadVerticle())
        plugins.await()
    }

}

fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(MainVerticle())
}


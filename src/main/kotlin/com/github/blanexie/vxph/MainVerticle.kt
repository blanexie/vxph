package com.github.blanexie.vxph

import com.github.blanexie.vxph.core.AbstractVerticle
import com.github.blanexie.vxph.core.PluginLoadVerticle
import com.github.blanexie.vxph.plugin.pluginFactory
import io.vertx.core.Vertx
import org.slf4j.LoggerFactory

/**
 * 启动三件事
 * 1. 加载框架verticle
 * 2. 加载插件
 * 3. 加载flow
 */
class MainVerticle : AbstractVerticle("mainVerticle", "0", "0") {

    private val log = LoggerFactory.getLogger(this::class.java)

    override suspend fun handleEnd() {
        vertx.deployVerticle(pluginFactory.buildCoreJdbcPlugin())
            .onComplete {
                vertx.deployVerticle(PluginLoadVerticle())
            }
    }

}

fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(MainVerticle())
}


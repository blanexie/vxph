package com.github.blanexie.vxph

import cn.hutool.setting.Setting
import cn.hutool.setting.SettingUtil
import com.github.blanexie.vxph.core.FlowLoadVerticle
import com.github.blanexie.vxph.core.PluginLoadVerticle
import com.github.blanexie.vxph.plugin.inbund.CronSchedulePlugin
import com.github.blanexie.vxph.plugin.inbund.HttpServerPlugin
import com.github.blanexie.vxph.plugin.outbund.JdbcPlugin
import io.vertx.core.AbstractVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
import io.vertx.kotlin.coroutines.awaitResult
import org.slf4j.LoggerFactory


/**
 * 启动三件事
 * 1. 加载框架verticle
 * 2. 加载插件
 * 3. 加载flow
 */
class MainVerticle : AbstractVerticle() {

  private val log = LoggerFactory.getLogger(this::class.java)
  private val setting = SettingUtil.get("vxph.properties")
  override fun start(startPromise: Promise<Void>) {
    val jdbcUrl = setting.getStr("vxph.database.jdbc.url")
    val username = setting.getStr("vxph.database.username", "")
    val password = setting.getStr("vxph.database.password", "")
    val maxPoolSize = setting.getInt("vxph.database.maxPoolSize", 16)
    val jdbcPlugin = JdbcPlugin(jdbcUrl, username, password, maxPoolSize)

    vertx.deployVerticle(jdbcPlugin) {
      if (it.succeeded()) {
        vertx.deployVerticle(PluginLoadVerticle())
        vertx.deployVerticle(FlowLoadVerticle())
      } else {
        log.info("vxph core jdbcPlugin load ERROR", it.cause())
      }
    }
  }

  private suspend fun loadCoreJdbcPlugin() {
    val jdbcUrl = setting.getStr("vxph.database.jdbc.url")
    val username = setting.getStr("vxph.database.username", "")
    val password = setting.getStr("vxph.database.password", "")
    val maxPoolSize = setting.getInt("vxph.database.maxPoolSize", 16)
    val jdbcPlugin = JdbcPlugin(jdbcUrl, username, password, maxPoolSize)

    val result = awaitResult<String> {
      vertx.deployVerticle(jdbcPlugin, it)
    }

  }


}

fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(MainVerticle())
}


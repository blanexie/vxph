package com.github.blanexie.vxph

import com.github.blanexie.vxph.plugin.inbund.CronSchedulePlugin
import com.github.blanexie.vxph.plugin.inbund.HttpServerPlugin
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
import org.slf4j.LoggerFactory

class MainVerticle : AbstractVerticle() {

  val log = LoggerFactory.getLogger(this::class.java)
  override fun start(startPromise: Promise<Void>) {
    log.info("vertx start..........")
    vertx.deployVerticle(CronSchedulePlugin("0/5 * * * * ?"))
    log.info("vertx CronSchedulePlugin load end  ..........")
    vertx.deployVerticle(HttpServerPlugin("/api/send", 11017))
    log.info("vertx HttpServerPlugin load end  ..........")
  }

}

fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(MainVerticle())
}


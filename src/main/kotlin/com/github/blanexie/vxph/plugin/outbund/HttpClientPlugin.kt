package com.github.blanexie.vxph.plugin.outbund

import io.vertx.kotlin.coroutines.CoroutineVerticle

class HttpClientPlugin : CoroutineVerticle() {


  override suspend fun start() {
    val consumer = vertx.eventBus().localConsumer<String>("a.b.c")
    consumer.handler { message ->

      message.reply("pong")
    }


  }

}

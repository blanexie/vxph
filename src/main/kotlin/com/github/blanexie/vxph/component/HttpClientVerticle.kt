package com.github.blanexie.vxph.component

import io.vertx.kotlin.coroutines.CoroutineVerticle

class HttpClientVerticle : CoroutineVerticle() {


  override suspend fun start() {
    val consumer = vertx.eventBus().localConsumer<String>("a.b.c")
    consumer.handler { message ->

      message.reply("pong")
    }


  }

}

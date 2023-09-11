package com.github.blanexie.vxph.component

import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle
import java.util.function.Function

abstract class AbstractEventBusVerticle(
  private val topic: String
) :
  CoroutineVerticle() {


  override suspend fun start() {
    val consumer = vertx.eventBus().localConsumer<String>(topic)
    consumer.handler { message ->
      val result = this.handle(message)
      message.reply(JsonObject.of("code", 200, "data", result).toString())
    }
  }

  abstract fun handle(message: Message<String>): String

}

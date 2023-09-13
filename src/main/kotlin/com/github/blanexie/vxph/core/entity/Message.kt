package com.github.blanexie.vxph.core.entity

import cn.hutool.core.util.IdUtil
import io.vertx.core.eventbus.impl.codecs.JsonObjectMessageCodec
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject


class Message(
  val sender: String,
  val receiver: String,
  val data: Map<String, Any> = hashMapOf(),
  val id: Long = IdUtil.getSnowflakeNextId(),
  var type: String = "send"  // or reply
)

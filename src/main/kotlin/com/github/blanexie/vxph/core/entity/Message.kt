package com.github.blanexie.vxph.core.entity

import cn.hutool.core.util.IdUtil


data class Message(
  val receiver: String,
  val sender: String,
  val data: Map<String, Any> = hashMapOf(),
  val id: Long = IdUtil.getSnowflakeNextId(),
)

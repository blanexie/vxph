package com.github.blanexie.vxph.core.entity

data class ReplyMessage(
  val messageId: List<String>,
  val receiver: String,
  val data: Map<String, Any>
)

package com.github.blanexie.vxph.core.entity

import cn.hutool.core.util.IdUtil
import io.vertx.core.eventbus.impl.codecs.JsonObjectMessageCodec
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject


class Message(
    var sender: String,
    var receiver: String,
    var data: HashMap<String, Any> = hashMapOf<String, Any>(),
    val id: Long = IdUtil.getSnowflakeNextId(),
    var type: MessageType = MessageType.send  // or reply
) {
    override fun toString(): String {
        return "Message{id:${id} , type:$type , sender:$sender , receiver:$receiver , data:$data}"
    }

    fun toReplyMessage(): Message {
        return Message(receiver, sender, id = id, type = MessageType.reply)
    }

    fun copy(): Message {
        return Message(sender, receiver, data, id, type)
    }


}

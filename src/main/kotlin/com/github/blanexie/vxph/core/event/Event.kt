package com.github.blanexie.vxph.core.event

import cn.hutool.core.map.MapUtil
import com.github.blanexie.vxph.core.objectMapper
import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message

const val CoreEventChannel = "vxph.core.eventbus.channel"
const val VerticleLoadCompleteEventType = "VerticleLoadComplete"

/**
 * 所有已经加载完成的 verticle
 */
data class Event(val type: String, val data: String? = null, val channel: String = CoreEventChannel) {

    fun send(vertx: Vertx) {
        vertx.eventBus().send(channel, objectMapper.writeValueAsString(this))
    }

    fun publish(vertx: Vertx) {
        vertx.eventBus().publish(channel, objectMapper.writeValueAsString(this))
    }

}

fun Message<String>.toEvent(): Event {
    val body = this.body()
    val readValue = objectMapper.readValue(body, Map::class.java)
    val channel = MapUtil.get(readValue, "channel", String::class.java)
    val type = MapUtil.get(readValue, "type", String::class.java)
    val data = MapUtil.get(readValue, "data", String::class.java)
    return Event(type, data, channel)
}
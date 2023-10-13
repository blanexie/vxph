package com.github.blanexie.vxph.tracker

import cn.hutool.core.util.HexUtil
import cn.hutool.db.Entity
import io.vertx.core.net.SocketAddress
import java.nio.ByteBuffer


const val peerExpireMinutes = 15
const val peerAnnounceIntervalMinutes = 15


fun Map<String, Any>.toEntity(tableName: String): Entity {
    val entity = Entity.create(tableName)
    this.forEach { (t, u) ->
        entity.set(t, u)
    }
    return entity
}


const val EVENT_START: String = "start"
const val EVENT_COMPLETE: String = "complete"
const val EVENT_STOP: String = "stop"
const val EVENT_EMPTY: String = "empty"


fun SocketAddress.toIpAddrMap(): Map<String, *> {
    val hostAddress = this.hostAddress()
    val port = this.port()
    val split = hostAddress.split(".")
    if (split.size == 4) {
        return mapOf(
            "ipv4" to byteArrayOf(split[0].toByte(), split[1].toByte(), split[2].toByte(), split[3].toByte()),
            "port" to port
        )
    }
    val split1 = hostAddress.split(":")
    if (split1.size == 8) {
        val allocate = ByteBuffer.allocate(16)
        split1.forEach {
            if (it == "0") {
                allocate.put(0).put(0)
                return@forEach
            }
            val decodeHex = HexUtil.decodeHex(it)
            if (decodeHex.size == 1) {
                allocate.put(0).put(decodeHex)
            } else if (decodeHex.size == 2) {
                allocate.put(decodeHex)
            }
        }
        return mapOf(
            "ipv6" to allocate.array(),
            "port" to port
        )
    }
    throw Error("无法解析本ip地址")
}


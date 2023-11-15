package com.github.blanexie.vxph.torrent


const val announceIntervalMinute = 10 * 60
const val peerActiveExpireMinute = announceIntervalMinute * 2

//started 种子开始下载
//completed 种子下载完成，开始做种
//stopped 种子停止下载 / 做种，不再活动
//empty 和没有此字段的情况完全相同
const val Event_Start = "started"
const val Event_Completed = "completed"
const val Event_Stopped = "stopped"
const val Event_Empty = "empty"


const val Announce_Url_Code ="Announce_Url_Code"

enum class IpType {
    IPV4, IPV6
}

fun parseInfoHash(encoded: String): String {
    return try {
        val r = StringBuilder()
        var i = 0
        while (i < encoded.length) {
            val c = encoded[i]
            if (c == '%') {
                r.append(encoded[i + 1])
                r.append(encoded[i + 2])
                i += 2
            } else {
                r.append(String.format("%02x", c.code))
            }
            i++
        }
        r.toString().lowercase()
    } catch (e: Exception) {
        throw IllegalArgumentException("Failed to decode info_hash: $encoded")
    }
}

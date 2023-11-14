package com.github.blanexie.vxph.torrent

import cn.hutool.core.util.HexUtil
import cn.hutool.core.util.URLUtil


const val announceIntervalMinute = 10 * 60
const val peerActiveExpireMinute = announceIntervalMinute * 2

//started, completed或stopped之一
const val Event_Start = "started"
const val Event_Completed = "completed"
const val Event_Stopped = "stopped"
const val Event_Empty = "empty"

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
                i = i + 2
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


fun main() {
    //passkey=aaaaaaaaaaaaaaaaaa&info_hash=%d8%c0%da%8bc%14%ff%c5t%02%fcZ%013%a5%dc%a1%9d%b5%dd&peer_id=-qB4550-4t-~kDHM1tRg&port=16881&uploaded=167871&downloaded=0&left=0&corrupt=0&key=3AF74B03&numwant=200&compact=1&no_peer_id=1&supportcrypto=1&redundant=0
    val infoHash = "%d8%c0%da%8bc%14%ff%c5t%02%fcZ%013%a5%dc%a1%9d%b5%dd"
    val toByteArray = URLUtil.decode(infoHash).toByteArray(Charsets.US_ASCII)
    val encodeHexStr = HexUtil.encodeHexStr(toByteArray)
    println(encodeHexStr)

    val parseInfoHash = parseInfoHash(infoHash)
    println(parseInfoHash)
}
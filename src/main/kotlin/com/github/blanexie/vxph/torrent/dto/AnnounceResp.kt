package com.github.blanexie.vxph.torrent.dto

import cn.hutool.core.util.ByteUtil
import cn.hutool.core.util.HexUtil
import cn.hutool.core.util.StrUtil
import com.github.blanexie.vxph.common.bencode
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.exception.VxphException
import java.nio.ByteBuffer

class AnnounceResp(
    val interval: Int,
    var failReason: String?,
    val peers: List<PeerResp>,
    val peers6: List<PeerResp>,
) {

    fun toBencodeByte(compact: Int = 1): ByteArray {
        val mapOf = hashMapOf<String, Any>("interval" to interval)
        if (StrUtil.isEmpty(failReason)) {
            if (compact == 1) {
                if (peers.isNotEmpty()) {
                    mapOf["peers"] = peers
                }
                if (peers6.isNotEmpty()) {
                    mapOf["peers6"] = peers6
                }
                return bencode.encode(mapOf)
            } else {
                if (peers.isNotEmpty()) {
                    val allocate = ByteBuffer.allocate(peers.size * 6)
                    peers.forEach {
                        allocate.put(it.toBytes())
                    }
                    mapOf["peers"] = allocate.array()
                }
                if (peers6.isNotEmpty()) {
                    val allocate = ByteBuffer.allocate(peers.size * 18)
                    peers6.forEach {
                        allocate.put(it.toBytes())
                    }
                    mapOf["peers6"] = allocate.array()
                }
                return bencode.encode(mapOf)
            }
        } else {
            mapOf["fail reason"] = failReason!!
            return bencode.encode(mapOf)
        }
    }

}

class PeerResp(val peerId: String, val ip: String, val port: Int) {

    fun toBytes(): ByteArray {
        if (ip.contains(".")) {
            val byteArray = ByteArray(6)
            val split = ip.split(".")
            byteArray[0] = split[0].toByte()
            byteArray[1] = split[1].toByte()
            byteArray[2] = split[2].toByte()
            byteArray[3] = split[3].toByte()
            val intToBytes = ByteUtil.intToBytes(port)
            byteArray[4] = intToBytes[2]
            byteArray[5] = intToBytes[3]
            return byteArray
        } else if (ip.contains(":")) {
            val allocate = ByteBuffer.allocate(18)
            ip.split(":").forEach {
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
            val intToBytes = ByteUtil.intToBytes(port)
            allocate.put(intToBytes[2])
            allocate.put(intToBytes[3])
            return allocate.array()
        } else {
            throw VxphException(SysCode.IpError)
        }
    }

}
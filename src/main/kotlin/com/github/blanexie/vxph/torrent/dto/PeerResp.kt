package com.github.blanexie.vxph.torrent.dto

import cn.hutool.core.util.ByteUtil
import cn.hutool.core.util.HexUtil
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.exception.VxphException
import com.github.blanexie.vxph.torrent.IpType
import java.nio.ByteBuffer

class PeerResp(
    val peerId: String,
    val ip: String,
    val port: Short,
    val type: IpType,
) {

    fun toBytes(): ByteArray {
        if (ip.contains(".")) {
            return toIpv4Bytes()
        } else if (ip.contains(":")) {
            return toIpv6Bytes()
        } else {
            throw VxphException(SysCode.IpError)
        }
    }

    private fun toIpv4Bytes(): ByteArray {
        val byteArray = ByteArray(6)
        val split = ip.split(".")
        byteArray[0] = split[0].toByte()
        byteArray[1] = split[1].toByte()
        byteArray[2] = split[2].toByte()
        byteArray[3] = split[3].toByte()
        val intToBytes = ByteUtil.shortToBytes(port)
        byteArray[4] = intToBytes[0]
        byteArray[5] = intToBytes[1]
        return byteArray
    }

    private fun toIpv6Bytes(): ByteArray {
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
        val intToBytes = ByteUtil.shortToBytes(port)
        allocate.put(intToBytes[0])
        allocate.put(intToBytes[1])
        return allocate.array()
    }

}
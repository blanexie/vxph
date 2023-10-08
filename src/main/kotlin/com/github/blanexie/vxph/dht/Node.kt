package com.github.blanexie.vxph.dht

import java.net.InetSocketAddress
import kotlin.experimental.xor

data class Node(
    val nodeId: NodeId,
    var lastChange: Long,
    var ip4: InetSocketAddress? = null,
    var ip6: InetSocketAddress? = null
) {

    fun getIdByte(): ByteArray {
        return nodeId.key
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Node
        return nodeId == other.nodeId
    }

    override fun hashCode(): Int {
        return nodeId.hashCode()
    }

    fun getCompactInfo(isIp6: Boolean = false): ByteArray? {
        return if (isIp6) {
            compactInfo(ip6)
        } else {
            compactInfo(ip4)
        }
    }

    private fun compactInfo(ip: InetSocketAddress?): ByteArray? {
        if (ip == null) {
            return null
        }
        val address = ip.address.address
        val byteArray = ByteArray(address.size + 2)
        address.forEachIndexed { index, byte ->
            byteArray[index] = byte
        }
        val port = ip.port
        val f = (port ushr 8) and 0xFF
        val s = port and 0xFF
        byteArray[address.size] = f.toByte()
        byteArray[address.size + 1] = s.toByte()
        return byteArray
    }


}


data class NodeId(val key: ByteArray) {


    fun difference(target: NodeId): Int {
        var distanceVal = 0
        var bits = 24
        for (i in 0..19) {
            val byte = key[i] xor target.key[i]
            if (byte.toInt() == 0) {
                distanceVal += 8
            } else {
                while (byte.toInt().shl(bits + 1) != 0) {
                    bits++
                }
                break
            }
        }
        distanceVal = distanceVal + bits - 24
        return distanceVal
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NodeId

        return key.contentEquals(other.key)
    }

    override fun hashCode(): Int {
        return key.contentHashCode()
    }


}

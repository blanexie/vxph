package com.github.blanexie.vxph.dht

import cn.hutool.core.util.HexUtil
import kotlin.experimental.xor
import kotlin.math.abs

data class NodeId(val key: ByteArray) {

    var nodeStr: String? = null

    fun difference(target: NodeId): Int {
        var distanceVal = 0
        var bits = 0
        for (i in 0..19) {
            val byte = key[i] xor target.key[i]

            if (byte.toInt() == 0) {
                distanceVal += 8
            } else {
                while (abs(byte.toInt()).ushr(bits + 1) != 0) {
                    bits++
                }
                break
            }
        }
        distanceVal = distanceVal + 8 - bits
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


    override fun toString(): String {
        if (nodeStr == null) {
            nodeStr = HexUtil.encodeHexStr(key)
        }
        return nodeStr!!
    }

}



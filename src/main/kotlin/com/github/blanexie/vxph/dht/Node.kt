package com.github.blanexie.vxph.dht

import java.math.BigInteger

data class Node(
    val id: ByteArray,
    val lastChange: Long,         // 最新的活跃时间
    val difference: BigInteger,   //距离
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node

        if (!id.contentEquals(other.id)) return false
        if (difference != other.difference) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.contentHashCode()
        result = 31 * result + difference.hashCode()
        return result
    }

}





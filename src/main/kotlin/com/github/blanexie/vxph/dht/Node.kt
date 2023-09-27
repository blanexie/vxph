package com.github.blanexie.vxph.dht

import java.math.BigInteger
import java.net.InetSocketAddress

data class Node(
    val id: ByteArray,
    var lastChange: Long,
    var inetSocketAddress: InetSocketAddress
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node

        if (!id.contentEquals(other.id)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.contentHashCode()
        return result
    }
}




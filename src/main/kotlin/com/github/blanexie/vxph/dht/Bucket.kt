package com.github.blanexie.vxph.dht

import cn.hutool.cache.CacheUtil
import cn.hutool.cache.impl.TimedCache
import cn.hutool.crypto.digest.DigestUtil
import java.math.BigInteger
import java.nio.ByteBuffer
import java.util.*
import kotlin.experimental.xor

class Bucket(
    val nodeId: ByteArray,
    val before: PriorityQueue<Node>,
    val after: PriorityQueue<Node>,
    val tCache: TimedCache<String, String>
) {
    constructor(code: String) : this(
        nodeId = DigestUtil.sha1(code),
        before = PriorityQueue<Node>(8) { o1, o2 -> o1.difference.divide(o2.difference).signum() },
        after = PriorityQueue<Node>(8) { o1, o2 -> o1.difference.divide(o2.difference).signum() },
        tCache = CacheUtil.newTimedCache<String, String>(15 * 60 * 1000)
    )

    fun findNodes(target: ByteArray): List<ByteArray> {
        val difference = difference(target)
        return if (difference.signum() > 0) {
            after.map { it.id }.toList()
        } else {
            before.map { it.id }.toList()
        }
    }

    private fun difference(id2: ByteArray): BigInteger {
        val byteArray = ByteArray(20)
        for (i in 0..19) {
            val byte = nodeId[i]
            val byte2 = id2[i]
            val b = byte xor byte2
            byteArray[i] = b
        }
        return BigInteger(byteArray)
    }

    fun addNode(id: ByteArray) {
        val difference = difference(id)
        val node = Node(id, System.currentTimeMillis(), difference)
        if (difference.signum() > 0) {
            after.remove(node)
            after.offer(node)
        } else {
            before.remove(node)
            before.offer(node)
        }
    }
}

fun Map<String, Any>.readString(key: String): String {
    val any = this[key] as ByteBuffer
    return String(any.array())
}

fun Map<String, Any>.readByteArray(key: String): ByteArray {
    val any = this[key] as ByteBuffer
    return any.array()
}

fun Map<String, Any>.readMap(key: String): Map<String, Any> {
    return this[key] as Map<String, Any>
}
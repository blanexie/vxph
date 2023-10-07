package com.github.blanexie.vxph.dht

import cn.hutool.cache.CacheUtil
import cn.hutool.cache.impl.TimedCache
import cn.hutool.core.collection.CollUtil
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import kotlin.experimental.xor

class KBucket(
    val nodeId: ByteArray,
    val bucketMap: HashMap<Int, ArrayList<Node>> = hashMapOf(),
    val tCache: TimedCache<String, String> = CacheUtil.newTimedCache<String, String>(30 * 1000)
) {



    fun findNodes(target: ByteArray): List<ByteArray> {
        val difference = difference(target)
        val get: List<Node> = bucketMap.getOrDefault(difference, listOf())
        return get.map { it.id }.toList()
    }

    private fun difference(target: ByteArray): Int {
        var distanceVal = 0
        var bits = 24
        for (i in 0..19) {
            val byte = nodeId[i] xor target[i]
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

    fun addNode(id: ByteArray, inetSocketAddress: InetSocketAddress) {
        val difference = difference(id)
        val nodeList = bucketMap.computeIfAbsent(difference) {
            arrayListOf()
        }

        val node = CollUtil.findOne(nodeList) {
            it.id.contentEquals(id)
        }
        node?.let {
            node.lastChange = System.currentTimeMillis()
            node.inetSocketAddress = inetSocketAddress
        }
        if (node == null) {
            nodeList.add(Node(id, System.currentTimeMillis(), inetSocketAddress))
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
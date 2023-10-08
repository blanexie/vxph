package com.github.blanexie.vxph.dht

import cn.hutool.cache.CacheUtil
import cn.hutool.cache.impl.TimedCache
import cn.hutool.core.collection.CollUtil
import java.net.InetSocketAddress
import java.nio.ByteBuffer

class KBucket(
    val nodeId: NodeId,
    val bucketMap: HashMap<Int, ArrayList<Node>> = hashMapOf(),
    //key 是 t , value 是远端的 地址
    val tCache: TimedCache<String, TReqInfo> = CacheUtil.newTimedCache(30 * 1000)
) {


    fun findNodes(target: NodeId): List<Node> {
        val difference = nodeId.difference(target)
        return bucketMap.getOrDefault(difference, listOf())
    }


    fun addNode(node: Node) {
        val difference = nodeId.difference(node.nodeId)
        val nodeList = bucketMap.computeIfAbsent(difference) {
            arrayListOf()
        }

        val nodeF = CollUtil.findOne(nodeList) {
            it.nodeId == node.nodeId
        }
        if (nodeF == null) {
            nodeList.add(node)
        } else {
            nodeF.lastChange = System.currentTimeMillis()
            node.ip4?.let {
                nodeF.ip4 = it
            }
            node.ip6?.let {
                nodeF.ip6 = it
            }
        }
    }

    fun addNode(id: ByteArray, ip4: InetSocketAddress) {
        addNode(Node(NodeId(id), System.currentTimeMillis(), ip4))
    }
}

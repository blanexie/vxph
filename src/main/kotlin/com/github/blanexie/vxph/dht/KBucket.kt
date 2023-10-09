package com.github.blanexie.vxph.dht

import cn.hutool.cache.CacheUtil
import cn.hutool.cache.impl.TimedCache
import cn.hutool.core.codec.Base64
import cn.hutool.core.collection.CollUtil
import com.github.blanexie.vxph.dht.message.FindNodeRequest
import com.github.blanexie.vxph.dht.message.PingRequest
import java.net.InetSocketAddress
import java.util.SortedSet

class KBucket(
    val nodeId: NodeId,
    val bucketMap: HashMap<Int, SortedSet<Node>> = hashMapOf(),
    //key 是 t , value 是远端的 地址
    val tCache: TimedCache<String, TReqInfo> = CacheUtil.newTimedCache(30 * 1000)
) {


    /**
     * 规整Bucket。 并返回缺少数据的桶的
     */
    fun regularity(): List<FindNodeRequest> {
        val distinct = hashSetOf<String>()
        val ret = arrayListOf<FindNodeRequest>()
        var currentNode = Node(nodeId, System.currentTimeMillis(), initNodeInetSocketAddress)
        for (i in 0..159) {
            val nodes = bucketMap.getOrDefault(i, sortedSetOf())
            //移除超出数量的Node
            while (nodes.size > bucketSize) {
                val last = nodes.last()
                nodes.remove(last)
            }

            if (nodes.isNotEmpty()) {
                currentNode = nodes.first()
            }

            if (nodes.size < bucketSize / 2) {
                val targetNodeId = buildTargetNodeId(i)
                if (distinct.add(Base64.encode(targetNodeId.key))) {
                    //数量过少， 返回第一个用户查找
                    ret.add(FindNodeRequest(currentNode, targetNodeId, this))
                }
            }

        }




        return ret
    }


    private fun buildTargetNodeId(index: Int): NodeId {
        val key = nodeId.key
        val start = index / 8
        val y = index % 8
        val t = key[start].toInt() ushr y shl y
        val byteArray = ByteArray(20)
        for (i in 0..19) {
            if (i < start) {
                byteArray[i] = key[i]
            }
            if (i == start) {
                byteArray[i] = t.toByte()
            }
            if (i > start) {
                byteArray[i] = 0
            }
        }
        return NodeId(byteArray)
    }


    /**
     * 找到长时间未活跃的Node
     */
    fun findUnUsedNode(): List<PingRequest> {
        val currentTimeMillis = System.currentTimeMillis()
        val nodes = bucketMap.map { m ->
            m.value.filter {
                val between = currentTimeMillis - it.lastChange
                between > nodeUnUsedTime
            }.toList()
        }.flatMap { it.asIterable() }.map { PingRequest(it, this) }
        return nodes
    }

    fun removeNode(node: Node) {
        val difference = nodeId.difference(node.nodeId)
        val nodeList = bucketMap.getOrDefault(difference, arrayListOf())
        nodeList.remove(node)
    }

    fun findNodes(target: NodeId): Set<Node> {
        val difference = nodeId.difference(target)
        return bucketMap.getOrDefault(difference, sortedSetOf())
    }


    fun addNode(node: Node) {
        val difference = nodeId.difference(node.nodeId)
        val nodeList = bucketMap.computeIfAbsent(difference) {
            sortedSetOf()
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
        node.upgradeVersion()
    }

    fun addNode(id: ByteArray, ip4: InetSocketAddress) {
        addNode(Node(NodeId(id), System.currentTimeMillis(), ip4))
    }
}

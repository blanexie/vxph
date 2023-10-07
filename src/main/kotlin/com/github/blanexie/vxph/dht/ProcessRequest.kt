package com.github.blanexie.vxph.dht

import cn.hutool.core.util.IdUtil
import java.net.InetSocketAddress

class ProcessRequest(val bucket: KBucket) {
    fun replyPack(dictionary: Map<String, Any>, inetSocketAddress: InetSocketAddress): ByteArray? {
        val q = dictionary.readString("q")
        when (q) {
            "ping" -> return processPing(dictionary, inetSocketAddress)
            "find_node" -> return processFindNode(dictionary, inetSocketAddress)
            "get_peers" -> return processGetPeers(dictionary, inetSocketAddress)
            // "announce_peer" -> replyPack(ctx, msg) { processAnnouncePeer(dictionary) }
        }
        return null
    }

    private fun processFindNode(dict: Map<String, Any>, inetSocketAddress: InetSocketAddress): ByteArray {
        val a = dict.readMap("a")

        val target = a.readByteArray("target")
        val findNodes = bucket.findNodes(target)

        val rBody = mutableMapOf<String, Any>()
        rBody["nodes"] = findNodes

        val id = a.readByteArray("id")
        bucket.addNode(id, inetSocketAddress)
        rBody["id"] = bucket.nodeId

        val resp = mapOf("t" to dict["t"], "y" to "r", "r" to rBody)
        return bencode.encode(resp)
    }

    private fun processPing(dict: Map<String, Any>, inetSocketAddress: InetSocketAddress): ByteArray {
        val resp = mapOf("t" to dict["y"], "y" to "r", "r" to mapOf("id" to bucket.nodeId))
        return bencode.encode(resp)
    }

    private fun processGetPeers(dict: Map<String, Any>, inetSocketAddress: InetSocketAddress): ByteArray {
        val a = dict.readMap("a")
        val id = a.readByteArray("id")
        bucket.addNode(id, inetSocketAddress)

        val mutableMapOf = mutableMapOf<String, Any>()
        val infoHash = a.readByteArray("info_hash")
        val findNodes = bucket.findNodes(infoHash)
        mutableMapOf["nodes"] = findNodes
        mutableMapOf["id"] = bucket.nodeId
        mutableMapOf["token"] = IdUtil.fastSimpleUUID()

        val resp = mapOf("t" to dict["t"], "y" to "r", "r" to mutableMapOf)
        return bencode.encode(resp)
    }

    private fun processAnnouncePeer(dict: Map<String, Any>, inetSocketAddress: InetSocketAddress) {


    }
}
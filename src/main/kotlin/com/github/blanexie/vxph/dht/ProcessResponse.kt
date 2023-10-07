package com.github.blanexie.vxph.dht

import java.net.InetSocketAddress
import java.nio.ByteBuffer

class ProcessResponse(val bucket: KBucket) {
    fun replyPack(dictionary: Map<String, Any>, inetSocketAddress: InetSocketAddress): ByteArray? {
        val t = dictionary.readString("t")
        val responseType = bucket.tCache.get(t)
        when (responseType) {
            "ping" -> processPing(dictionary, inetSocketAddress)
            "find_node" -> processFindNode(dictionary, inetSocketAddress)
            "get_peers" -> processGetPeers(dictionary, inetSocketAddress)
            // "announce_peer" -> replyPack(ctx, msg) { processAnnouncePeer(dictionary) }
        }
        return null
    }

    //{"t":"aa", "y":"r", "r": {"id":"mnopqrstuvwxyz123456"}}
    private fun processPing(dict: Map<String, Any>, inetSocketAddress: InetSocketAddress) {
        val r = dict.readMap("r")
        val id = r.readByteArray("id")
        bucket.addNode(id, inetSocketAddress)
    }

    //{"t":"aa", "y":"r", "r": {"id":"0123456789abcdefghij", "nodes": "def456..."}}
    private fun processFindNode(dict: Map<String, Any>, inetSocketAddress: InetSocketAddress) {
        val r = dict.readMap("r")
        val id = r.readByteArray("id")
        bucket.addNode(id, inetSocketAddress)

        val nodes = r["nodes"] as ByteBuffer

        var offset = 0
        while (offset < nodes.capacity()) {
            val byteArray = ByteArray(20)
            nodes.get(byteArray, offset, 20)
            offset += 20

            val ipBytes = ByteArray(4)
            nodes.get(ipBytes, offset, 4)
            val ipStr = ipBytes.map { toString() }
                .joinToString(separator = ".")
            offset += 4

            val portBytes = ByteArray(2)
            nodes.get(ipBytes, offset, 2)
            val byte = portBytes[0]
            var port = byte.toInt() shl 8
            port += portBytes[1]
            offset += 2

            bucket.addNode(byteArray, InetSocketAddress(ipStr, port))
            offset += 20
        }
    }

    /**
     * Response with peers = {"t":"aa", "y":"r", "r": {"id":"abcdefghij0123456789", "token":"aoeusnth", "values": ["axje.u", "idhtnm"]}}
     * bencoded = d1:rd2:id20:abcdefghij01234567895:token8:aoeusnth6:valuesl6:axje.u6:idhtnmee1:t2:aa1:y1:re
     * Response with closest nodes = {"t":"aa", "y":"r", "r": {"id":"abcdefghij0123456789", "token":"aoeusnth", "nodes": "def456..."}}
     * bencoded = d1:rd2:id20:abcdefghij01234567895:nodes9:def456...5:token8:aoeusnthe1:t2:aa1:y1:re
     */
    private fun processGetPeers(dict: Map<String, Any>, inetSocketAddress: InetSocketAddress) {
        val r = dict.readMap("r")
        val id = r.readByteArray("id")
        bucket.addNode(id, inetSocketAddress)
        val nodesAny = r["nodes"]
        nodesAny?.let {
            val nodes = it as ByteBuffer
            var offset = 0
            while (offset < nodes.capacity()) {
                val byteArray = ByteArray(20)
                nodes.get(byteArray, offset, 20)
                offset += 20

                val ipBytes = ByteArray(4)
                nodes.get(ipBytes, offset, 4)
                val ipStr = ipBytes.map { toString() }
                    .joinToString(separator = ".")
                offset += 4

                val portBytes = ByteArray(2)
                nodes.get(ipBytes, offset, 2)
                val byte = portBytes[0]
                var port = byte.toInt() shl 8
                port += portBytes[1]
                offset += 2

                bucket.addNode(byteArray, InetSocketAddress(ipStr, port))
            }
        }
    }

}

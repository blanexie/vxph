package com.github.blanexie.vxph.dht

import java.nio.ByteBuffer

class ProcessResponse(val bucket: Bucket) {
    fun replyPack(dictionary: Map<String, Any>): ByteArray? {

        val t = dictionary.readString("t")
        val responseType = bucket.tCache.get(t)
        when (responseType) {
            "ping" -> processPing(dictionary)
            "find_node" -> processFindNode(dictionary)
            "get_peers" -> processGetPeers(dictionary)
            // "announce_peer" -> replyPack(ctx, msg) { processAnnouncePeer(dictionary) }
        }
        return null
    }

    //{"t":"aa", "y":"r", "r": {"id":"mnopqrstuvwxyz123456"}}
    private fun processPing(dict: Map<String, Any>) {
        val r = dict.readMap("r")
        val id = r.readByteArray("id")
        bucket.addNode(id)
    }

    //{"t":"aa", "y":"r", "r": {"id":"0123456789abcdefghij", "nodes": "def456..."}}
    private fun processFindNode(dict: Map<String, Any>) {
        val r = dict.readMap("r")
        val id = r.readByteArray("id")
        bucket.addNode(id)

        val nodes = r["nodes"] as ByteBuffer

        var offset = 0
        while (offset < nodes.capacity()) {
            val byteArray = ByteArray(20)
            nodes.get(byteArray, offset, 20)
            bucket.addNode(byteArray)
            offset += 20
        }
    }

    /**
     * Response with peers = {"t":"aa", "y":"r", "r": {"id":"abcdefghij0123456789", "token":"aoeusnth", "values": ["axje.u", "idhtnm"]}}
     * bencoded = d1:rd2:id20:abcdefghij01234567895:token8:aoeusnth6:valuesl6:axje.u6:idhtnmee1:t2:aa1:y1:re
     * Response with closest nodes = {"t":"aa", "y":"r", "r": {"id":"abcdefghij0123456789", "token":"aoeusnth", "nodes": "def456..."}}
     * bencoded = d1:rd2:id20:abcdefghij01234567895:nodes9:def456...5:token8:aoeusnthe1:t2:aa1:y1:re
     */
    private fun processGetPeers(dict: Map<String, Any>) {
        val r = dict.readMap("r")
        val id = r.readByteArray("id")
        bucket.addNode(id)
        val nodesAny = r["nodes"]
        nodesAny?.let {
            val nodes = it as ByteBuffer
            var offset = 0
            while (offset < nodes.capacity()) {
                val byteArray = ByteArray(20)
                nodes.get(byteArray, offset, 20)
                bucket.addNode(byteArray)
                offset += 20
            }
        }
    }

}

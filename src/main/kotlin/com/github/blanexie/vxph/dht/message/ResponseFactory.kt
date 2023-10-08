package com.github.blanexie.vxph.dht.message

import com.github.blanexie.vxph.dht.KBucket
import com.github.blanexie.vxph.dht.readString
import java.net.InetSocketAddress

class ResponseFactory(val kBucket: KBucket) {

    fun process(dictionary: Map<String, Any>, inetSocketAddress: InetSocketAddress): BaseResponse? {
        val t = dictionary.readString("t")
        val tReqInfo = kBucket.tCache.get(t) ?: return null
        val method = tReqInfo.method
        when (method) {
            "ping" -> return PingResponse(dictionary, inetSocketAddress)
            "find_node" -> return FindNodeResponse(dictionary, inetSocketAddress)
            "get_peers" -> return GetPeerResponse(dictionary, inetSocketAddress)
            // "announce_peer" -> replyPack(ctx, msg) { processAnnouncePeer(dictionary) }
        }
        return null
    }


}
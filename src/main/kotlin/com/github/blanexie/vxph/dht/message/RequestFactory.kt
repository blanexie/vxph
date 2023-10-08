package com.github.blanexie.vxph.dht.message

import com.github.blanexie.vxph.dht.Node
import com.github.blanexie.vxph.dht.readString
import java.net.InetSocketAddress

class RequestFactory {

  fun process(dictionary: Map<String, Any>, inetSocketAddress: InetSocketAddress): BaseRequest? {
    val q = dictionary.readString("q")
    when (q) {
      "ping" -> return PingRequest(dictionary, inetSocketAddress)
      "find_node" -> return FindNodeRequest(dictionary, inetSocketAddress)
      "get_peers" -> return GetPeerRequest(dictionary, inetSocketAddress)
      // "announce_peer" -> replyPack(ctx, msg) { processAnnouncePeer(dictionary) }
    }
    return null
  }

}

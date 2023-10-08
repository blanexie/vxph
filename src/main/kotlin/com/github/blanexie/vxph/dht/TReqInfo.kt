package com.github.blanexie.vxph.dht

import java.net.InetSocketAddress

class TReqInfo(
    val t: String,
    val method: String,
    var ip4: InetSocketAddress? = null,
    var ip6: InetSocketAddress? = null,
    var nodeId: NodeId? = null,
    var token: String? = null,

    ) {
}
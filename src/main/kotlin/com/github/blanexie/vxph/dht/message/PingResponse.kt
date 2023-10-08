package com.github.blanexie.vxph.dht.message

import com.github.blanexie.vxph.dht.*
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetSocketAddress

class PingResponse(
    t: String,
    y: String,
    r: Map<String, ByteArray>,
    ip4: InetSocketAddress? = null,
    ip6: InetSocketAddress? = null,
) : BaseResponse(t, y, r, ip4, ip6) {

    constructor(dict: Map<String, Any>, ip: InetSocketAddress) : this(
        dict["t"] as String,
        dict["y"] as String,
        dict["r"] as Map<String, ByteArray>,
        ip4 = if (ip.address is Inet4Address) {
            ip
        } else {
            null
        },
        ip6 = if (ip.address is Inet6Address) {
            ip
        } else {
            null
        }
    )

    override fun apply(kBucket: KBucket) {
        val bytes = r.readByteArray("id")
        kBucket.addNode(Node(NodeId(bytes), System.currentTimeMillis(), ip4, ip6))
    }

}
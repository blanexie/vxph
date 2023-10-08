package com.github.blanexie.vxph.dht.message

import com.github.blanexie.vxph.dht.*
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetSocketAddress

class FindNodeResponse(
    t: String,
    y: String,
    r: Map<String, Any>,
    ip4: InetSocketAddress? = null,
    ip6: InetSocketAddress? = null,
) : BaseResponse(t, y, r, ip4, ip6) {

    constructor(dict: Map<String, Any>, ip: InetSocketAddress) : this(
        dict.readString("t"),
        dict.readString("y"),
        dict.readMap("r"),
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

        val nodes = r.readByteArray("nodes")
        var index = 0
        while (index < nodes.size) {
            val nodeId = nodes.readNodeId(index)
            index += 20
            val inetSocketAddress = nodes.readIp4(index)
            index += 6
            val node = Node(
                nodeId,
                System.currentTimeMillis(),
                inetSocketAddress
            )
            kBucket.addNode(node)
        }

    }

}

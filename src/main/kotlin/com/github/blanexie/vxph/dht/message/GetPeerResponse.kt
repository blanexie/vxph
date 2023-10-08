package com.github.blanexie.vxph.dht.message

import cn.hutool.core.util.ByteUtil
import com.github.blanexie.vxph.dht.*
import io.netty.util.NetUtil
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetSocketAddress

class GetPeerResponse(
    t: String,
    y: String,
    r: Map<String, Any>,
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

        if (!r.containsKey("nodes")) {
            return
        }

        val token = r.readString("token")
        val nodes = r.readByteArray("nodes")
        var index = 0
        while (index < nodes.size) {
            val nodeId = NodeId(nodes.copyOfRange(index, index + 20))
            index += 20
            val ip4 = nodes.copyOfRange(index, index + 4)
            index += 4
            val port = nodes.copyOfRange(index, index + 2)
            index += 2
            val node = Node(
                nodeId,
                System.currentTimeMillis(),
                InetSocketAddress(NetUtil.bytesToIpAddress(ip4), ByteUtil.bytesToInt(port))
            )
            kBucket.addNode(node)
        }

    }

}
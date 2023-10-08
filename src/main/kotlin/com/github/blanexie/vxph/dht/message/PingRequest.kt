package com.github.blanexie.vxph.dht.message

import cn.hutool.core.util.IdUtil
import com.github.blanexie.vxph.dht.*
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetSocketAddress


/**
 * ping
 * The most basic query is a ping. "q" = "ping" A ping query has a single argument,
 * "id" the value is a 20-byte string containing the senders node ID in network byte order.
 * The appropriate response to a ping has a single key "id" containing the node ID of the responding node.
 *
 * arguments:  {"id" : "<querying nodes id>"}
 *
 * response: {"id" : "<queried nodes id>"}
 * Example Packets
 *
 * ping Query = {"t":"aa", "y":"q", "q":"ping", "a":{"id":"abcdefghij0123456789"}}
 * bencoded = d1:ad2:id20:abcdefghij0123456789e1:q4:ping1:t2:aa1:y1:qe
 * Response = {"t":"aa", "y":"r", "r": {"id":"mnopqrstuvwxyz123456"}}
 * bencoded = d1:rd2:id20:mnopqrstuvwxyz123456e1:t2:aa1:y1:re
 */
class PingRequest(
    t: String,
    y: String,
    q: String,
    a: Map<String, Any>,
    ip4: InetSocketAddress? = null,
    ip6: InetSocketAddress? = null,
) : BaseRequest(t, y, q, a, ip4, ip6) {

    constructor(dict: Map<String, Any>, ip: InetSocketAddress) : this(
        dict.readString("t"),
        dict.readString("y"),
        dict.readString("q"),
        dict.readMap("a"),
        ip4 = if (ip.address is Inet4Address) {
            ip
        } else {
            null
        },
        ip6 = if (ip.address is Inet6Address) {
            ip
        } else {
            null
        },
    )

    constructor(node: Node, kBucket: KBucket) : this(
        IdUtil.getSnowflakeNextIdStr(),
        "q",
        "ping",
        mapOf("id" to kBucket.nodeId.key),
        node.ip4,
        node.ip6
    )


    override fun apply(kBucket: KBucket): PingResponse {
        val bytes = a.readByteArray("id")
        kBucket.addNode(Node(NodeId(bytes), System.currentTimeMillis(), ip4, ip6))
        return PingResponse(t, "r", mapOf("id" to kBucket.nodeId.key), ip4, ip6)
    }


}
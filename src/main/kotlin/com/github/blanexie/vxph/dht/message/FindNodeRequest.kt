package com.github.blanexie.vxph.dht.message

import cn.hutool.core.util.IdUtil
import com.github.blanexie.vxph.dht.KBucket
import com.github.blanexie.vxph.dht.Node
import com.github.blanexie.vxph.dht.NodeId
import com.github.blanexie.vxph.dht.readByteArray
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetSocketAddress

/**
 * Example Packets
 *
 * find_node Query = {"t":"aa", "y":"q", "q":"find_node", "a": {"id":"abcdefghij0123456789", "target":"mnopqrstuvwxyz123456"}}
 * bencoded = d1:ad2:id20:abcdefghij01234567896:target20:mnopqrstuvwxyz123456e1:q9:find_node1:t2:aa1:y1:qe
 * Response = {"t":"aa", "y":"r", "r": {"id":"0123456789abcdefghij", "nodes": "def456..."}}
 * bencoded = d1:rd2:id20:0123456789abcdefghij5:nodes9:def456...e1:t2:aa1:y1:re
 */
class FindNodeRequest(
    t: String,
    y: String,
    q: String,
    a: Map<String, ByteArray>,
    ip4: InetSocketAddress? = null,
    ip6: InetSocketAddress? = null,
) : BaseRequest(t, y, q, a, ip4, ip6) {

    constructor(dict: Map<String, Any>, ip: InetSocketAddress) : this(
        dict["t"] as String,
        dict["y"] as String,
        dict["q"] as String,
        dict["a"] as Map<String, ByteArray>,
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

    constructor(node: Node, target: NodeId, kBucket: KBucket) : this(
        IdUtil.getSnowflakeNextIdStr(),
        "q",
        "find_node",
        mapOf("id" to kBucket.nodeId.key, "target" to target.key),
        node.ip4,
        node.ip6
    )


    override fun apply(kBucket: KBucket): FindNodeResponse {
        val bytes = a.readByteArray("id")
        kBucket.addNode(Node(NodeId(bytes), System.currentTimeMillis(), ip4, ip6))
        val target = a.readByteArray("target")
        val findNodes = kBucket.findNodes(NodeId(target))

        val toList = findNodes.mapNotNull { it.getCompactInfo() }
            .toList()
        val size = toList.size * 26
        val nodes = ByteArray(size)
        toList.forEachIndexed { index, bytes ->
            bytes.forEachIndexed { index1, byte ->
                nodes[index * 26 + index1] = byte
            }
        }

        return FindNodeResponse(t, "r", mapOf("id" to kBucket.nodeId.key, "nodes" to nodes), ip4, ip6)
    }

}
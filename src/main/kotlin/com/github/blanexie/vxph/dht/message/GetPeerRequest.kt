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
 * Example Packets:
 *
 * get_peers Query = {"t":"aa", "y":"q", "q":"get_peers", "a": {"id":"abcdefghij0123456789", "info_hash":"mnopqrstuvwxyz123456"}}
 * bencoded = d1:ad2:id20:abcdefghij01234567899:info_hash20:mnopqrstuvwxyz123456e1:q9:get_peers1:t2:aa1:y1:qe
 * Response with peers = {"t":"aa", "y":"r", "r": {"id":"abcdefghij0123456789", "token":"aoeusnth", "values": ["axje.u", "idhtnm"]}}
 * bencoded = d1:rd2:id20:abcdefghij01234567895:token8:aoeusnth6:valuesl6:axje.u6:idhtnmee1:t2:aa1:y1:re
 * Response with closest nodes = {"t":"aa", "y":"r", "r": {"id":"abcdefghij0123456789", "token":"aoeusnth", "nodes": "def456..."}}
 * bencoded = d1:rd2:id20:abcdefghij01234567895:nodes9:def456...5:token8:aoeusnthe1:t2:aa1:y1:re
 */
class GetPeerRequest(
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

    constructor(node: Node, infoHash: NodeId, kBucket: KBucket) : this(
        IdUtil.getSnowflakeNextIdStr(),
        "q",
        "get_peers",
        mapOf("id" to kBucket.nodeId.key, "info_hash" to infoHash.key),
        node.ip4,
        node.ip6
    )

    override fun apply(kBucket: KBucket): GetPeerResponse {
        val id = a.readByteArray("id")
        kBucket.addNode(Node(NodeId(id), System.currentTimeMillis(), ip4, ip6))
        val infoHash = a.readByteArray("info_hash")

        val findNodes = kBucket.findNodes(NodeId(infoHash))
        val toList = findNodes.mapNotNull { it.getCompactInfo() }
            .toList()
        val size = toList.size * 26
        val nodes = ByteArray(size)
        toList.forEachIndexed { index, bytes ->
            bytes.forEachIndexed { index1, byte ->
                nodes[index * 26 + index1] = byte
            }
        }

        return GetPeerResponse(
            t,
            "r",
            mapOf("id" to kBucket.nodeId.key, "token" to System.currentTimeMillis().toString(), "nodes" to nodes),
            ip4,
            ip6
        )
    }


}
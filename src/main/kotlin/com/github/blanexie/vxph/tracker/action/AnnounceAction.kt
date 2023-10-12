package com.github.blanexie.vxph.tracker.action

import com.github.blanexie.vxph.dht.bencode
import com.github.blanexie.vxph.dht.encodeToBuffer
import com.github.blanexie.vxph.dht.nodeRefreshTime
import com.github.blanexie.vxph.tracker.http.Path
import com.github.blanexie.vxph.tracker.entity.PeerEntity
import com.github.blanexie.vxph.tracker.entity.UserTorrentEntity
import com.github.blanexie.vxph.tracker.objectMapper
import com.github.blanexie.vxph.tracker.peerExpireMinutes
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import java.time.Duration
import java.time.LocalDateTime

@Path("/")
class AnnounceAction() {

    /**
     * GET /announce?peer_id=aaaaaaaaaaaaaaaaaaaa&info_hash=aaaaaaaaaaaaaaaaaaaa
     * &port=6881&left=0&downloaded=100&uploaded=0&compact=1
     */
    @Path("/announce", method = "GET")
    fun process(request: HttpServerRequest): HttpServerResponse {
        val response = request.response()
        val peerEntity = getPeerFromRequest(request)
        //检查是否符合要求
        val userTorrentEntity = UserTorrentEntity.findByPasskey(peerEntity.passkey, peerEntity.infoHash)
        //校验passkey存在否，
        if (userTorrentEntity == null) {
            val mapOf = mapOf("failure reason" to "not found user or torrent")
            response.write(bencode.encodeToBuffer(mapOf))
            return response
        }
        //校验 peerId是否正确， 如果换客户端，需要用户主动到网站去申报， 并且清除之前的数据， 原则上是不允许换客户端的
        if (userTorrentEntity.peerId == null) {
            userTorrentEntity.updatePeerId(peerEntity.peerId)
        } else if (userTorrentEntity.peerId != peerEntity.peerId) {
            val mapOf = mapOf("failure reason" to "not allow change client")
            response.write(bencode.encodeToBuffer(mapOf))
            return response
        }

        //保存更新上报的数据
        peerEntity.upsert()
        val peerEntities = PeerEntity.findByInfoHash(peerEntity.infoHash)

        val now = LocalDateTime.now()
        val peers = peerEntities.sortedBy(PeerEntity::left)
            .filter { Duration.between(it.updateTime, now).toMinutes() < peerExpireMinutes }
            .stream().limit(75)

        val compact = request.getParam("compact", "1")
        if (compact == "1") {
            peers.map {
                val peerId = it.peerId
                it.remoteAddress
                peerId
            }
        }


        response.end(objectMapper.writeValueAsString(peerEntities))
        return response
    }


    private fun getPeerFromRequest(request: HttpServerRequest): PeerEntity {
        val passkey = request.getParam("passkey")
        val remoteAddress = request.remoteAddress()
        val peerId = request.getParam("peer_id")
        val infoHash = request.getParam("info_hash")
        val port = request.getParam("port")
        val downloaded = request.getParam("downloaded")
        val left = request.getParam("left")
        val uploaded = request.getParam("uploaded")
        val event = request.getParam("event")
        return PeerEntity(
            passkey, peerId, infoHash, remoteAddress.toString(), port.toInt(), downloaded.toLong(),
            left.toLong(), uploaded.toLong(), event, LocalDateTime.now(), LocalDateTime.now(), 0
        )
    }


}
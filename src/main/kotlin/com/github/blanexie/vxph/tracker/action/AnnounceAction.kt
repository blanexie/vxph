package com.github.blanexie.vxph.tracker.action

import com.github.blanexie.vxph.tracker.http.Path
import com.github.blanexie.vxph.tracker.entity.PeerEntity
import com.github.blanexie.vxph.tracker.objectMapper
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import java.time.LocalDateTime

@Path("/")
class AnnounceAction() {

    /**
     * GET /announce?peer_id=aaaaaaaaaaaaaaaaaaaa&info_hash=aaaaaaaaaaaaaaaaaaaa
     * &port=6881&left=0&downloaded=100&uploaded=0&compact=1
     */
    @Path("/announce", method = "GET")
    fun process(request: HttpServerRequest): HttpServerResponse {
        val peerEntity = getPeerFromRequest(request)
        //检查是否符合要求


        peerEntity.upsert()
        val peerEntities = PeerEntity.findByInfoHash(peerEntity.infoHash)


        val compact = request.getParam("compact")
        val response = request.response()
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
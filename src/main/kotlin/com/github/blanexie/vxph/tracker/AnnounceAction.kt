package com.github.blanexie.vxph.tracker

import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import java.time.LocalDateTime

@Path("/announce")
class AnnounceAction() {


    /**
     * GET /announce?peer_id=aaaaaaaaaaaaaaaaaaaa&info_hash=aaaaaaaaaaaaaaaaaaaa
     * &port=6881&left=0&downloaded=100&uploaded=0&compact=1
     */
    fun process(request: HttpServerRequest): HttpServerResponse {
        val peerEntity = getPeerFromRequest(request)
        peerEntity.insertOrUpdate()
        val findByInfoHash = PeerEntity.findByInfoHash(peerEntity.infoHash)
        val compact = request.getParam("compact")
        val response = request.response()
        response.end(objectMapper.writeValueAsString(findByInfoHash))
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
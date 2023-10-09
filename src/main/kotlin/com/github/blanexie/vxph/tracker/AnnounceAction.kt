package com.github.blanexie.vxph.tracker

import io.vertx.core.http.HttpServerRequest
import io.vertx.jdbcclient.JDBCPool

class AnnounceAction(val jdbcPool: JDBCPool) {


    /**
     * GET /announce?peer_id=aaaaaaaaaaaaaaaaaaaa&info_hash=aaaaaaaaaaaaaaaaaaaa
     * &port=6881&left=0&downloaded=100&uploaded=0&compact=1
     */
    fun process(request: HttpServerRequest) {
        val passkey = request.getParam("passkey")
        val remoteAddress = request.remoteAddress()
        val peerId = request.getParam("peer_id")
        val infoHash = request.getParam("info_hash")
        val port = request.getParam("port")
        val downloaded = request.getParam("downloaded")
        val left = request.getParam("left")
        val uploaded = request.getParam("uploaded")
        val compact = request.getParam("compact")
        val event = request.getParam("event")




    }


}
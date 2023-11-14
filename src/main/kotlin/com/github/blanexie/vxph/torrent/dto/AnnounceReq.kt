package com.github.blanexie.vxph.torrent.dto


class AnnounceReq(
    val peerId: String, val infoHash: String, val passKey: String, val left: Long, val downloaded: Long,
    val uploaded: Long, val compact: Int, val event: String, val remoteAddr: String, val remotePort: Int
) {


}
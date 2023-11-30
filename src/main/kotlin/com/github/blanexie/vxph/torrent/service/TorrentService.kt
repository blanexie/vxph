package com.github.blanexie.vxph.torrent.service

import com.github.blanexie.vxph.torrent.controller.dto.ScrapeResp
import com.github.blanexie.vxph.torrent.entity.Peer
import com.github.blanexie.vxph.torrent.entity.Post
import com.github.blanexie.vxph.torrent.entity.Torrent
import com.github.blanexie.vxph.user.entity.User
import java.io.OutputStream


interface TorrentService {

    fun findAllByInfoHashIn(infoHash: List<String>): List<Torrent>

    fun findByInfoHash(infoHash: String): Torrent?

    fun writeTorrentBytes(peer: Peer, torrent: Torrent, outputStream: OutputStream)

    fun saveTorrent(torrentMap: Map<String, Any>, loginUserId: Long, post: Post, title: String): Torrent

    /**
     * 批量获取
     */
    fun processScrape(infoHash: List<String>): ScrapeResp

    fun updateData(incomplete:Int, complete:Int, downloaded:Int, infoHash: String)
}
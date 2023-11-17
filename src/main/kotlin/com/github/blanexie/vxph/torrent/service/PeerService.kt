package com.github.blanexie.vxph.torrent.service

import com.github.blanexie.vxph.torrent.controller.dto.AnnounceReq
import com.github.blanexie.vxph.torrent.controller.dto.AnnounceResp
import com.github.blanexie.vxph.torrent.entity.Peer
import com.github.blanexie.vxph.torrent.entity.Torrent
import com.github.blanexie.vxph.user.entity.User
import java.time.LocalDateTime


interface PeerService {

    fun processAnnounce(announceReq: AnnounceReq): AnnounceResp

    /**
     * 返回存活的节点
     */
    fun findActivePeers(infoHash: String): AnnounceResp

    fun findInfoHashAfter(time: LocalDateTime): List<String>

    fun findAllByInfoHash(infoHash: String): List<Peer>

    fun findByInfoHashAndUserId(infoHash: String, userId: Long):Peer?

    fun save(peer: Peer):Peer


    fun checkAndSave(user: User, torrent: Torrent):Peer
}

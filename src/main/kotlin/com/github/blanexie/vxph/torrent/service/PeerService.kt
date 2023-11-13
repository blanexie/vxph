package com.github.blanexie.vxph.torrent.service

import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.exception.VxphException
import com.github.blanexie.vxph.torrent.Event_Completed
import com.github.blanexie.vxph.torrent.Event_Start
import com.github.blanexie.vxph.torrent.dto.AnnounceResp
import com.github.blanexie.vxph.torrent.dto.PeerResp
import com.github.blanexie.vxph.torrent.repository.PeerRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PeerService(val peerRepository: PeerRepository) {

    fun processAnnounceReq(
        peerId: String, infoHash: String, passKey: String, left: Long, downloaded: Long,
        uploaded: Long, compact: Int, event: String, remoteAddr: String, remotePort: Int
    ): AnnounceResp? {
        val peer = peerRepository.findByPassKey(passKey)
            ?: return AnnounceResp(10, "There is no peer. Please download the torrent first.", emptyList(), emptyList())

        if (peer.infoHash != infoHash) {
            return AnnounceResp(10, "The torrent does not exist. Please confirm.", emptyList(), emptyList())
        }
        if (peer.peerId != null && peer.peerId != peerId) {
            return AnnounceResp(10, "Please log in to the website to confirm before changing the client.", emptyList(), emptyList())
        }
        peer.uploadTime = LocalDateTime.now()
        peer.peerId = peerId
        //判断是ipv4 还是ipv6
        if (remoteAddr.contains(".")) {
            peer.ipv4 = remoteAddr
        } else if (remoteAddr.contains(":")) {
            peer.ipv6 = remoteAddr
        } else {
            throw VxphException(SysCode.RemoteIpError)
        }
        peer.port = remotePort
        peer.left = left
        peer.downloaded = downloaded
        peer.uploaded = uploaded
        peer.event = event
        peerRepository.save(peer)
        return null
    }


    /**
     * 返回存活的节点
     */
    fun findActivePeers(infoHash: String): AnnounceResp {
        val peerList = peerRepository.findByInfoHashAndEventIn(infoHash, listOf(Event_Completed, Event_Start))
        val peers = arrayListOf<PeerResp>()
        val peers6 = arrayListOf<PeerResp>()
        peerList.sortedBy { it.uploadTime }.subList(0, 100).forEach {
            if (it.ipv4 != null) {
                peers.add(PeerResp(it.peerId!!, it.ipv4!!, it.port!!))
            }
            if (it.ipv6 != null) {
                peers6.add(PeerResp(it.peerId!!, it.ipv6!!, it.port!!))
            }
        }
        return AnnounceResp(10, null, peers, peers6)
    }

}
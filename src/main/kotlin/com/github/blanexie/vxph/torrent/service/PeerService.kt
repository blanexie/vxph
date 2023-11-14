package com.github.blanexie.vxph.torrent.service

import com.github.blanexie.vxph.torrent.*
import com.github.blanexie.vxph.torrent.dto.AnnounceReq
import com.github.blanexie.vxph.torrent.dto.AnnounceResp
import com.github.blanexie.vxph.torrent.dto.PeerResp
import com.github.blanexie.vxph.torrent.entity.Peer
import com.github.blanexie.vxph.torrent.repository.PeerRepository
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime

@Service
class PeerService(val peerRepository: PeerRepository) {

    fun processAnnounce(announceReq: AnnounceReq): AnnounceResp {
        val peer = peerRepository.findByPassKey(announceReq.passKey)
        val resp = this.checkPeer(announceReq, peer)
        if (resp != null) {
            return resp
        }

        peer!!

        peer.refresh(announceReq)
        peerRepository.save(peer)

        return findActivePeers(announceReq.infoHash)
    }




    /**
     * 校验传入的参数是否有问题
     */
    private fun checkPeer(announceReq: AnnounceReq, peer: Peer?): AnnounceResp? {
        if (peer == null) {
            return AnnounceResp(
                announceIntervalMinute,
                "There is no peer. Please download the torrent first.", emptyList(), emptyList()
            )
        }
        if (peer.infoHash != announceReq.infoHash) {
            return AnnounceResp(
                announceIntervalMinute,
                "The torrent does not exist. Please confirm.", emptyList(), emptyList()
            )
        }
        if (peer.peerId != null && peer.peerId != announceReq.peerId) {
            return AnnounceResp(
                announceIntervalMinute,
                "Please log in to the website to confirm before changing the client.", emptyList(), emptyList()
            )
        }
        return null
    }

    /**
     * 返回存活的节点
     */
    fun findActivePeers(infoHash: String): AnnounceResp {
        val peerList = peerRepository.findByInfoHashAndEventIn(infoHash, listOf(Event_Completed, Event_Start))
        val peers = arrayListOf<PeerResp>()
        val peers6 = arrayListOf<PeerResp>()
        val now = LocalDateTime.now()
        peerList.filter { Duration.between(now, it.uploadTime).toMinutes() < peerActiveExpireMinute }
            .sortedBy { it.uploadTime }.subList(0, 100)
            .forEach {
                it.toPeerResps().forEach { peerResp ->
                    if (peerResp.type == IpType.IPV6) {
                        peers6.add(peerResp)
                    }
                    if (peerResp.type == IpType.IPV4) {
                        peers.add(peerResp)
                    }
                }
            }
        return AnnounceResp(announceIntervalMinute, null, peers, peers6)
    }


}
package com.github.blanexie.vxph.torrent.service

import cn.hutool.core.net.NetUtil
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
        val remoteAddr = announceReq.remoteAddr
        if (remoteAddr.contains(".") && NetUtil.isInnerIP(remoteAddr)) {
            //内网ip地址和本地回环地址
            return AnnounceResp(
                announceIntervalMinute,
                "The download client ip address is an intranet address and cannot be processed", emptyList(), emptyList()
            )
        }
        if (remoteAddr.contains(":")) {
            //判断是否是ipv6的本地回环地址
//            val tr = remoteAddr.split(":").all { it == "0000" || it == "0" || it == "1" || it == "0001" }
//            if (tr) {
//                return AnnounceResp(
//                    announceIntervalMinute,
//                    "The download client ip address is an intranet address and cannot be processed", emptyList(), emptyList()
//                )
//            }
        }

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
        peerList.filter { Duration.between(it.uploadTime, now).toSeconds() < peerActiveExpireMinute }
            .sortedBy { it.uploadTime }.stream().limit(100)
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

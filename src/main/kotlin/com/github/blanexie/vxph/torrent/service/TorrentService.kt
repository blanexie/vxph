package com.github.blanexie.vxph.torrent.service

import cn.hutool.core.util.IdUtil
import com.github.blanexie.vxph.common.bencode
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.exception.VxphException
import com.github.blanexie.vxph.torrent.Event_Completed
import com.github.blanexie.vxph.torrent.Event_Empty
import com.github.blanexie.vxph.torrent.Event_Start
import com.github.blanexie.vxph.torrent.announceIntervalMinute
import com.github.blanexie.vxph.torrent.dto.ScrapeData
import com.github.blanexie.vxph.torrent.dto.ScrapeResp
import com.github.blanexie.vxph.torrent.entity.Peer
import com.github.blanexie.vxph.torrent.entity.Torrent
import com.github.blanexie.vxph.torrent.repository.PeerRepository
import com.github.blanexie.vxph.torrent.repository.TorrentRepository
import com.github.blanexie.vxph.user.service.CodeService
import com.github.blanexie.vxph.user.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.File
import java.nio.ByteBuffer
import java.time.LocalDateTime

@Service
class TorrentService(
    private val torrentRepository: TorrentRepository,
    private val peerRepository: PeerRepository,
    private val codeService: CodeService,
    private val userService: UserService,
    @Value("\${vxph.torrent.path}")
    val torrentPath: String,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 定时任务， 统计
     */
    @Scheduled(fixedRate = announceIntervalMinute * 1000L)
    fun scheduleProcessTorrentData() {
        log.info("定时统计torrent数据，定时任务开始")
        val time = LocalDateTime.now().minusSeconds(announceIntervalMinute * 1L)
        val infoHashList = peerRepository.findInfoHashAfter(time)
        infoHashList.forEach {
            val peers = peerRepository.findAllByInfoHash(it)
            //  目前做种人数
            var complete = 0
            //  目前正在下载人数
            var incomplete = 0
            //  曾经下载完成过的人数
            var downloaded = 0
            peers.forEach { p ->
                if (p.event == Event_Completed) {
                    complete++
                }
                if (p.event == Event_Start) {
                    incomplete++
                }
                if (p.left == 0L) {
                    downloaded++
                }
            }
            torrentRepository.updateData(incomplete, complete, downloaded, it)
        }
        log.info("定时统计torrent数据，定时任务结束")
    }

    fun findAllByInfoHashIn(infoHash: List<String>): List<Torrent> {
        if (infoHash.isEmpty()) {
            return emptyList()
        }
        return torrentRepository.findAllByInfoHashIn(infoHash)

    }

    fun buildTorrentBytes(infoHash: String, userId: Long): ByteBuffer {
        val torrent = torrentRepository.findByInfoHash(infoHash)
        if (torrent != null) {
            val passkey = IdUtil.fastSimpleUUID()
            val user = userService.findById(userId)!!
            val infoBytes = File("${torrentPath}/${infoHash}.torrent").readBytes()
            val torrentMap = hashMapOf<String, Any>()
            torrentMap["comment"] = "vxph torrent,  ${torrent.comment}"
            torrentMap["create date"] = torrent.creationDate
            torrentMap["create by"] = torrent.createdBy
            val announceUrl = codeService.findAnnounceUrl()
            if (announceUrl.isNotEmpty() && announceUrl.size == 1) {
                torrentMap["announce"] = "${announceUrl[0]}?passkey=$passkey"
            } else {
                torrentMap["announce"] = "${announceUrl[0]}?passkey=$passkey"
                torrentMap["announce-list"] = announceUrl.map { "${it}?passkey=$passkey" }.toList()
            }
            val encode = bencode.encode(torrentMap)
            val result = ByteBuffer.allocate(encode.size + infoBytes.size)
            result.put(encode)
            result.put(encode.size - 1, infoBytes)
            result.put(101.toByte())
            val peer = Peer(
                null, torrent.infoHash, passkey, null, null, null, null, 0, 0,
                0, Event_Empty, LocalDateTime.now(), torrent, user
            )
            peerRepository.save(peer)
            return result
        } else {
            throw VxphException(SysCode.TorrentNotExist)
        }

    }

    fun save(torrent: Torrent) {
        torrentRepository.save(torrent)
    }

    /**
     * 批量获取
     */
    fun processScrape(infoHash: List<String>): ScrapeResp {
        val torrents = torrentRepository.findAllByInfoHashIn(infoHash)
        val scrapeDataMap = hashMapOf<String, ScrapeData>()
        torrents.forEach {
            scrapeDataMap[it.infoHash] = ScrapeData(it.complete, it.incomplete, it.downloaded)
        }
        return ScrapeResp(scrapeDataMap)
    }

}
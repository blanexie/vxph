package com.github.blanexie.vxph.torrent.service

import com.github.blanexie.vxph.common.bencode
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.exception.VxphException
import com.github.blanexie.vxph.torrent.Event_Completed
import com.github.blanexie.vxph.torrent.Event_Start
import com.github.blanexie.vxph.torrent.Event_Stopped
import com.github.blanexie.vxph.torrent.announceIntervalMinute
import com.github.blanexie.vxph.torrent.dto.ScrapeData
import com.github.blanexie.vxph.torrent.dto.ScrapeResp
import com.github.blanexie.vxph.torrent.entity.Torrent
import com.github.blanexie.vxph.torrent.repository.PeerRepository
import com.github.blanexie.vxph.torrent.repository.TorrentRepository
import com.github.blanexie.vxph.user.service.CodeService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.sqlite.core.Codes
import java.io.File
import java.nio.ByteBuffer
import java.time.LocalDateTime

@Service
class TorrentService(
    private val torrentRepository: TorrentRepository,
    private val peerRepository: PeerRepository,
    private val codeService: CodeService,
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

    fun buildTorrentBytes(infoHash: String): ByteBuffer {
        val torrent = torrentRepository.findByInfoHash(infoHash)
        if (torrent != null) {
            val infoBytes = File("${torrentPath}/${infoHash}").readBytes()
            val torrentMap = hashMapOf<String, Any>()
            torrentMap["comment"] = "vxph torrent,  ${torrent.comment}"
            torrentMap["create date"] = torrent.creationDate
            torrentMap["create by"] = torrent.createdBy
            val announceUrl = codeService.findAnnounceUrl()
            if (announceUrl.isNotEmpty() && announceUrl.size == 1) {
                torrentMap["announce"] = announceUrl[0]
            } else {
                torrentMap["announce"] = announceUrl[0]
                torrentMap["announce-list"] = announceUrl
            }
            val encode = bencode.encode(torrentMap)
            val result = ByteBuffer.allocate(encode.size + infoBytes.size)
            result.put(encode)
            result.put(encode.size - 1, infoBytes)
            result.put(65.toByte())
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
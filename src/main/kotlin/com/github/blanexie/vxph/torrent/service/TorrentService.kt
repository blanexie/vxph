package com.github.blanexie.vxph.torrent.service

import com.github.blanexie.vxph.torrent.Event_Completed
import com.github.blanexie.vxph.torrent.Event_Start
import com.github.blanexie.vxph.torrent.Event_Stopped
import com.github.blanexie.vxph.torrent.announceIntervalMinute
import com.github.blanexie.vxph.torrent.dto.ScrapeData
import com.github.blanexie.vxph.torrent.dto.ScrapeResp
import com.github.blanexie.vxph.torrent.repository.PeerRepository
import com.github.blanexie.vxph.torrent.repository.TorrentRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TorrentService(val torrentRepository: TorrentRepository, val peerRepository: PeerRepository) {

    val log = LoggerFactory.getLogger(this::class.java)

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
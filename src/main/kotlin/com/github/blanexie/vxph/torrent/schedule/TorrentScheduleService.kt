package com.github.blanexie.vxph.torrent.schedule

import com.github.blanexie.vxph.torrent.service.PeerService
import com.github.blanexie.vxph.torrent.service.TorrentService
import com.github.blanexie.vxph.torrent.util.Event_Completed
import com.github.blanexie.vxph.torrent.util.Event_Start
import com.github.blanexie.vxph.torrent.util.announceIntervalMinute
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TorrentScheduleService(
    private val peerService: PeerService,
    private val torrentService: TorrentService,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 定时任务， 统计
     */
    @Scheduled(fixedRate = announceIntervalMinute * 1000L, initialDelay = announceIntervalMinute * 1000L)
    fun scheduleProcessTorrentData() {
        log.info("定时统计torrent数据，定时任务开始")
        val time = LocalDateTime.now().minusSeconds(announceIntervalMinute * 1L)
        val infoHashList = peerService.findInfoHashAfter(time)
        infoHashList.forEach {
            val peers = peerService.findAllByInfoHash(it)
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
            torrentService.updateData(incomplete, complete, downloaded, it)
        }
        log.info("定时统计torrent数据，定时任务结束")
    }
}
package com.github.blanexie.vxph.torrent.service

import com.github.blanexie.vxph.torrent.dto.ScrapeData
import com.github.blanexie.vxph.torrent.dto.ScrapeResp
import com.github.blanexie.vxph.torrent.repository.TorrentRepository
import org.springframework.stereotype.Service

@Service
class TorrentService(val torrentRepository: TorrentRepository) {


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
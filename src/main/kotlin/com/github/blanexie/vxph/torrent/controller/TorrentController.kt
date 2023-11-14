package com.github.blanexie.vxph.torrent.controller

import com.github.blanexie.vxph.common.web.InfoHashParam
import com.github.blanexie.vxph.torrent.service.TorrentService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping
@RestController
class TorrentController(val torrentService: TorrentService) {

    /**
     * 抓取
     */
    @RequestMapping("/scrape")
    fun scrape(
        @InfoHashParam("infoHash") infoHash: List<String>, request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val scrapeResp = torrentService.processScrape(infoHash)
        response.outputStream.write(scrapeResp.toBytes())
        response.flushBuffer()
        return
    }

}
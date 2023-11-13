package com.github.blanexie.vxph.torrent.controller

import cn.hutool.core.util.StrUtil
import com.github.blanexie.vxph.torrent.Event_Start
import com.github.blanexie.vxph.torrent.entity.Peer
import com.github.blanexie.vxph.torrent.service.PeerService
import com.github.blanexie.vxph.user.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam


@RequestMapping
@Controller
class PeerController(val peerService: PeerService) {


    /**
     * GET /announce?peer_id=aaaaaaaaaaaaaaaaaaaa&info_hash=aaaaaaaaaaaaaaaaaaaa
     * &port=6881&left=0&downloaded=100&uploaded=0&compact=1
     */
    @GetMapping("/announce")
    fun announce(
        @RequestParam peerId: String,
        @RequestParam infoHash: String,
        @RequestParam passKey: String,
        @RequestParam port: Int?,
        @RequestParam left: Long,
        @RequestParam downloaded: Long,
        @RequestParam uploaded: Long,
        @RequestParam compact: Int,
        @RequestParam(defaultValue = Event_Start) event: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {

        val remoteAddr = request.remoteAddr
        val remotePort = request.remotePort
        val announceResp = peerService.processAnnounceReq(
            peerId, infoHash, passKey, left, downloaded, uploaded, compact, event,
            remoteAddr, remotePort
        )
        if(announceResp!=null){

            response.outputStream.write()
            return
        }
        if (announceResp == null) {
            var announceResp = peerService.findActivePeers(infoHash)
        }

    }


}

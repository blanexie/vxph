package com.github.blanexie.vxph.torrent.controller

import cn.hutool.core.util.HexUtil
import com.github.blanexie.vxph.common.web.InfoHashParam
import com.github.blanexie.vxph.torrent.Event_Start
import com.github.blanexie.vxph.torrent.dto.AnnounceReq
import com.github.blanexie.vxph.torrent.service.PeerService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RequestMapping
@RestController
class PeerController(val peerService: PeerService) {

    val log = LoggerFactory.getLogger(this::class.java)

    /**
     * passkey=127ad347eb825b568c9a4f88e2b27eec
     * &info_hash=%7b%e7%5e%96%1b%96~%d45a%dd%d4%26%ebf%ca%9c%40.%1b
     * &peer_id=-qB4550-X6a!~L)pdkm8
     * &port=41326
     * &uploaded=0
     * &downloaded=0
     * &left=5334737323
     * &corrupt=0
     * &key=365222CD
     * &event=started
     * &numwant=200
     * &compact=1
     * &no_peer_id=1
     * &supportcrypto=1
     * &redundant=0
     * &ipv6=2408%3a820c%3a8f1b%3a9f80%3a1d10%3a436b%3a5229%3a336a
     * &ipv6=2408%3a820c%3a8f1b%3a9f80%3ac0ce%3ae696%3a5978%3a3a58
     *
     *  * remotePort: 54116
     *
     *
     * passkey=127ad347eb825b568c9a4f88e2b27eec
     * &info_hash=%7b%e7%5e%96%1b%96~%d45a%dd%d4%26%ebf%ca%9c%40.%1b
     * &peer_id=-qB4550-X6a!~L)pdkm8
     * &port=41326
     * &uploaded=0
     * &downloaded=0
     * &left=5334737323
     * &corrupt=0
     * &key=365222CD
     * &event=started
     * &numwant=200
     * &compact=1
     * &no_peer_id=1
     * &supportcrypto=1
     * &redundant=0
     *
     * remotePort: 54116
     * remoteAddr:127.0.0.1
     */
    @RequestMapping("/announce")
    fun announce(
        @RequestParam(name = "peer_id") peerId: String,
        @InfoHashParam(name = "info_hash") infoHash: String,
        @RequestParam(name = "passkey") passKey: String,
        @RequestParam port: Int?,
        @RequestParam left: Long,
        @RequestParam downloaded: Long,
        @RequestParam uploaded: Long,
        @RequestParam(defaultValue = "1") compact: Int,
        @RequestParam(defaultValue = Event_Start) event: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        log.info("receive announce， infoHash:{} ", infoHash)
        val remoteAddr = request.remoteAddr
        val remotePort = request.remotePort
        //build 请求对象，屏蔽请求层的信息
        val announceReq = AnnounceReq(
            peerId, infoHash, passKey, left, downloaded, uploaded, compact, event, remoteAddr, remotePort
        )
        //处理请求
        val announceResp = peerService.processAnnounce(announceReq)

        //返回响应
        response.outputStream.write(announceResp.toBytes(compact))
        response.flushBuffer()
        return
    }


}

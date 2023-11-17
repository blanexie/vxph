package com.github.blanexie.vxph.torrent.controller

import com.github.blanexie.vxph.torrent.service.PeerService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RequestMapping
@RestController
class PeerController(val peerService: PeerService) {

    val log = LoggerFactory.getLogger(this::class.java)



}

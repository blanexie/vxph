package com.github.blanexie.vxph.torrent.controller

import com.github.blanexie.vxph.common.entity.WebResp
import com.github.blanexie.vxph.torrent.service.LabelService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/label")
class LabelController(
    private val labelService: LabelService
) {

    @GetMapping("/list")
    fun list(): WebResp {
        val result = labelService.findAll()
        return WebResp.ok(result)
    }

}
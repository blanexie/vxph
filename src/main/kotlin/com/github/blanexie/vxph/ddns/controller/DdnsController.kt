package com.github.blanexie.vxph.ddns.controller

import cn.dev33.satoken.util.SaResult
import com.github.blanexie.vxph.ddns.service.DdnsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/ddns")
@RestController
class DdnsController(val ddnsService: DdnsService) {


    @GetMapping("/ips")
    fun findLocalIp(): SaResult {
        val findLocalIp = ddnsService.findLocalIp()
        val saResult = SaResult.ok()
        saResult.data = findLocalIp
        return saResult
    }

    @GetMapping("/findAliyunRecord")
    fun findAliyunRecord(@RequestParam recordId: String): SaResult {
        val findLocalIp = ddnsService.findAliyunRecord(recordId)
        val saResult = SaResult.ok()
        saResult.data = findLocalIp
        return saResult
    }


}
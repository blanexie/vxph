package com.github.blanexie.vxph.ddns.controller

import com.github.blanexie.vxph.common.entity.WebResp
import com.github.blanexie.vxph.ddns.entity.DomainRecord
import com.github.blanexie.vxph.ddns.service.DdnsService
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/ddns")
class DdnsController(val ddnsService: DdnsService) {

    @GetMapping("/ips")
    fun findLocalIp(): WebResp {
        val findLocalIp = ddnsService.findLocalIp()
        return WebResp.ok(findLocalIp)
    }

    @GetMapping("/findAliyunRecord")
    fun findAliyunRecord(@RequestParam recordId: String): WebResp {
        val recordIds = ddnsService.findAliyunRecord(recordId)
        return WebResp.ok(recordIds)
    }

    @GetMapping("/findRecords")
    fun findRecords(): WebResp {
        val result = ddnsService.findAll()
        return WebResp.ok(result)
    }

    @PostMapping("/updateRecord")
    fun updateRecord(@RequestBody domainRecord: DomainRecord): WebResp {
        val result = ddnsService.updateRecord(domainRecord)
        return WebResp.ok(result)
    }

    @GetMapping("/deleteRecord")
    fun deleteRecord(@RequestParam recordId: String): WebResp {
        ddnsService.deleteRecord(recordId)
        return WebResp.ok()
    }

    @PostMapping("/addRecord")
    fun addRecord(@RequestBody domainRecord: DomainRecord): WebResp {
        val result = ddnsService.addRecord(domainRecord)
        return WebResp.ok(result)
    }
}
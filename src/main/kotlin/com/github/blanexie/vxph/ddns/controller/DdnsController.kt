package com.github.blanexie.vxph.ddns.controller

import cn.dev33.satoken.util.SaResult
import com.github.blanexie.vxph.ddns.entity.DomainRecord
import com.github.blanexie.vxph.ddns.service.DdnsService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/ddns")
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

    @GetMapping("/findRecords")
    fun findRecords(): SaResult {
        val result = ddnsService.findAll()
        val saResult = SaResult.ok()
        saResult.data = result
        return saResult
    }

    @PostMapping("/updateRecord")
    fun updateRecord(@RequestBody domainRecord: DomainRecord): SaResult {
        val result = ddnsService.updateRecord(domainRecord)
        val saResult = SaResult.ok()
        saResult.data = result
        return saResult
    }

    @GetMapping("/deleteRecord")
    fun deleteRecord(@RequestParam recordId: String): SaResult {
        ddnsService.deleteRecord(recordId)
        return SaResult.ok()
    }

    @PostMapping("/addRecord")
    fun addRecord(@RequestBody domainRecord: DomainRecord): SaResult {
        val result = ddnsService.addRecord(domainRecord)
        val saResult = SaResult.ok()
        saResult.data = result
        return saResult
    }
}
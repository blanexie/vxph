package com.github.blanexie.vxph.ddns.controller

import com.github.blanexie.vxph.common.entity.WebResp
import com.github.blanexie.vxph.ddns.entity.DomainRecord
import com.github.blanexie.vxph.ddns.service.AliyunDdnsService
import com.github.blanexie.vxph.ddns.service.DdnsService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/ddns")
class DdnsController(val ddnsService: DdnsService, val aliyunDdnsService: AliyunDdnsService) {

    @GetMapping("/ips")
    fun findLocalIp(): WebResp {
        val findLocalIp = ddnsService.findLocalIp()
        return WebResp.ok(findLocalIp)
    }

    @GetMapping("/findRecords")
    fun findRecords(): WebResp {
        val result = ddnsService.findAll()
        return WebResp.ok(result)
    }

    @PostMapping("/addOrUpdate")
    fun addOrUpdate(@RequestBody domainRecord: DomainRecord): WebResp {
        val result = ddnsService.addOrUpdate(domainRecord)
        return WebResp.ok(result)
    }


    @GetMapping("/deleteRecord")
    fun deleteRecord(@RequestParam recordId: String): WebResp {
        ddnsService.deleteRecord(recordId)
        return WebResp.ok()
    }

    @GetMapping("/deleteAliyunRecord")
    fun deleteAliyunRecord(@RequestParam recordId: String): WebResp {
        aliyunDdnsService.deleteRecord(recordId)
        return WebResp.ok()
    }

    /**
     * 同步更新到阿里云
     */
    @GetMapping("/syncAliyunRecord")
    fun syncAliyunRecord(@RequestParam id: Long): WebResp {
        val result = ddnsService.syncRecord(id)
        return WebResp.ok(result)
    }

    /**
     * 同步从阿里云更新
     */
    @GetMapping("/downloadAliyunRecord")
    fun downloadAliyunRecord(@RequestParam recordId: String): WebResp {
        val result = ddnsService.downloadRecord(recordId)
        return WebResp.ok(result)
    }

}
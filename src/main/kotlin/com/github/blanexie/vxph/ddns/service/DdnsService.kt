package com.github.blanexie.vxph.ddns.service

import com.aliyun.alidns20150109.Client
import com.aliyun.alidns20150109.models.*
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.exception.VxphException
import com.github.blanexie.vxph.ddns.entity.DomainRecord
import com.github.blanexie.vxph.ddns.repositroy.DomainRecordRepository
import com.github.blanexie.vxph.ddns.util.httpClient
import jakarta.persistence.EntityManager
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDateTime

@Service
class DdnsService(
    val domainRecordRepository: DomainRecordRepository,
    val aliyunDdnsService: AliyunDdnsService,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 每30分钟执行一次
     */
    @Scheduled(cron = "0 0/15 * * * ?")
    fun schedule() {
        log.info("定时DDNS解析任务开始")
        val findLocalIp = findLocalIp()
        val ipv4 = findLocalIp["ipv4"]
        val ipv6 = findLocalIp["ipv6"]
        log.info("服务器ipv4：{}   ipv6:{}", ipv4, ipv6)
        val findAll = domainRecordRepository.findAll()
        findAll.forEach {
            if (it.type == "AAAA" && ipv6 != null && ipv6 != it.value) {
                it.value = ipv6
                this.syncRecord(it.id!!)
            }
            if (it.type == "A" && ipv4 != null && ipv4 != it.value) {
                it.value = ipv4
                this.syncRecord(it.id!!)
            }
        }
        log.info("定时DDNS解析任务结束")
    }

    fun downloadRecord(recordId: String): DomainRecord {
        val response = aliyunDdnsService.findRecord(recordId)
        var domainRecord = domainRecordRepository.findByRecordId(recordId)
        if (domainRecord == null) {
            domainRecord = DomainRecord(
                null, response.recordId, response.domainName, response.type, response.rr, response.value,
                response.ttl.toInt(), response.remark
            )
        } else {
            domainRecord.rr = response.rr
            domainRecord.ttl = response.ttl.toInt()
            domainRecord.domainName = response.domainName
            domainRecord.type = response.type
            domainRecord.value = response.value
            domainRecord.remark = response.remark?:""
        }
        domainRecordRepository.save(domainRecord)
        return domainRecord
    }

    fun syncRecord(id: Long) {
        val domainRecord = domainRecordRepository.findById(id).get()
        if (domainRecord.recordId == null) {
            val recordId = aliyunDdnsService.addRecord(domainRecord)
            domainRecord.recordId = recordId
            domainRecordRepository.save(domainRecord)
        } else {
            aliyunDdnsService.updateRecord(domainRecord)
            domainRecord.updateTime = LocalDateTime.now()
            domainRecordRepository.save(domainRecord)
        }
    }

    fun addOrUpdate(domainRecord: DomainRecord): DomainRecord {
        domainRecordRepository.save(domainRecord)
        return domainRecord
    }

    fun findAll(): List<DomainRecord> {
        val domainRecords = domainRecordRepository.findAll()
        return domainRecords.toList()
    }

    fun findLocalIp(): Map<String, String> {
        val result = hashMapOf<String, String>()
        try {
            val request = HttpRequest.newBuilder(URI.create("https://ipv4.ddnspod.com")).build()
            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            result["ipv4"] = response.body()
        } catch (e: Exception) {
            log.warn("获取ipv4异常， {}", e.message)
        }
        try {
            val request2 = HttpRequest.newBuilder(URI.create("https://v6.myip.la")).build()
            val response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString())
            result["ipv6"] = response2.body()
        } catch (e: Exception) {
            log.warn("获取ipv6异常， {}", e.message)
        }
        return result
    }

    fun deleteRecord(recordId: String) {
        domainRecordRepository.deleteByRecordId(recordId)
    }

}
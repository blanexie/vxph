package com.github.blanexie.vxph.ddns.service

import com.aliyun.alidns20150109.Client
import com.aliyun.alidns20150109.models.*
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.exception.VxphException
import com.github.blanexie.vxph.ddns.entity.DomainRecord
import com.github.blanexie.vxph.ddns.repositroy.DomainRecordRepository
import com.github.blanexie.vxph.ddns.util.httpClient
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class DdnsService(
    val domainRecordRepository: DomainRecordRepository,
    val client: Client
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
                this.updateRecord(it)
            }
            if (it.type == "A" && ipv4 != null && ipv4 != it.value) {
                it.value = ipv4
                this.updateRecord(it)
            }
        }
        log.info("定时DDNS解析任务结束")
    }


    fun addRecord(domainRecord: DomainRecord): DomainRecord {
        if (domainRecord.recordId != null) {
            throw VxphException(SysCode.RecordIdExist)
        }
        val addDomainRecordRequest = AddDomainRecordRequest()
        addDomainRecordRequest.ttl = domainRecord.ttl.toLong()
        addDomainRecordRequest.rr = domainRecord.rr
        addDomainRecordRequest.type = domainRecord.type
        addDomainRecordRequest.value = domainRecord.value
        addDomainRecordRequest.domainName = domainRecord.domainName
        val addDomainRecord = client.addDomainRecord(addDomainRecordRequest)
        if (addDomainRecord.statusCode != 200) {
            throw VxphException(SysCode.AliyunClientError)
        }
        domainRecord.recordId = addDomainRecord.body.recordId
        domainRecordRepository.save(domainRecord)
        return domainRecord
    }

    fun updateRecord(domainRecord: DomainRecord): DomainRecord {
        val updateDomainRecordRequest = UpdateDomainRecordRequest()
        updateDomainRecordRequest.ttl = domainRecord.ttl.toLong()
        updateDomainRecordRequest.rr = domainRecord.rr
        updateDomainRecordRequest.type = domainRecord.type
        updateDomainRecordRequest.value = domainRecord.value
        updateDomainRecordRequest.recordId = domainRecord.recordId

        val updateDomainRecord = client.updateDomainRecord(updateDomainRecordRequest)
        if (updateDomainRecord.statusCode != 200) {
            throw VxphException(SysCode.AliyunClientError, updateDomainRecord.statusCode.toString())
        }
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

    fun findAliyunRecord(recordId: String): DescribeDomainRecordInfoResponseBody {
        val describeDomainRecordInfo = DescribeDomainRecordInfoRequest()
        describeDomainRecordInfo.recordId = recordId

        val describeDomainRecords = client.describeDomainRecordInfo(describeDomainRecordInfo)
        return describeDomainRecords.body
    }

    fun deleteRecord(recordId: String) {
        val deleteDomainRecordRequest = DeleteDomainRecordRequest()
        deleteDomainRecordRequest.recordId = recordId
        val deleteDomainRecord = client.deleteDomainRecord(deleteDomainRecordRequest)
        if (deleteDomainRecord.statusCode == 200) {
            domainRecordRepository.deleteByRecordId(recordId)
        } else {
            throw VxphException(SysCode.AliyunClientError, deleteDomainRecord.statusCode.toString())
        }
    }

}
package com.github.blanexie.vxph.ddns.controller

import com.github.blanexie.vxph.core.Verticle
import com.github.blanexie.vxph.core.getProperty
import com.github.blanexie.vxph.ddns.entity.DomainRecordEntity
import com.github.blanexie.vxph.ddns.service.AliyunDnsService
import com.github.blanexie.vxph.ddns.service.WebIpAddrServiceImpl
import com.github.blanexie.vxph.core.web.Path
import com.github.blanexie.vxph.core.objectMapper
import com.github.blanexie.vxph.core.web.HttpVerticle
import io.vertx.core.http.HttpServerRequest
import org.slf4j.LoggerFactory
import java.time.LocalDateTime


@Verticle
class DDNSVerticle : HttpVerticle() {

    private val log = LoggerFactory.getLogger(this::class.java)
    val aliyunDnsService: AliyunDnsService = AliyunDnsService()
    val ipAddrService: WebIpAddrServiceImpl = WebIpAddrServiceImpl()
    val expireMinutes: Int = getProperty("vxph.ddns.aliyun.scheduleMinutes", 15)
    override suspend fun start() {
        enablePathRouter()
        vertx.setPeriodic(expireMinutes * 60 * 1000L) {
            this.schedule()
        }
    }

    /**
     * 查询域名的所有云解析记录
     */
    @Path("/ddns/findLocalIp")
    fun findLocalIp(request: HttpServerRequest): Map<String, String> {
        val ipv6 = ipAddrService.ipv6()
        val ipv4 = ipAddrService.ipv4()
        return mapOf("ipv6" to ipv6, "ipv4" to ipv4)
    }

    /**
     * 查询域名的所有云解析记录
     */
    @Path("/ddns/findDomainRecords")
    fun findDomainRecords(request: HttpServerRequest): Map<String, Any> {
        val domainName = request.getParam("domainName")
        val dbDomainRecords = DomainRecordEntity.findByDomain(domainName)
        val recordsResponseBody = aliyunDnsService.describeDomainRecords(domainName)
        return mapOf("dbDomainRecords" to dbDomainRecords, "aliyunDomainRecords" to recordsResponseBody)
    }

    /**
     * 添加云解析记录
     */
    @Path("/ddns/addDomainRecord")
    fun addDomainRecord(request: HttpServerRequest): Any {
        val domainName = request.getParam("domainName")
        val rr = request.getParam("rr")
        val type = request.getParam("type")
        val value = request.getParam("value")
        val ttl = request.getParam("ttl")
        val responseBody = aliyunDnsService.addDomainRecord(domainName, rr, type, value, ttl.toInt())
        return responseBody
    }

    /**
     * 删除云解析记录
     */
    @Path("/ddns/deleteDomainRecord")
    fun deleteDomainRecord(request: HttpServerRequest): Any {
        val recordId = request.getParam("recordId")
        val recordResponseBody = aliyunDnsService.deleteDomainRecord(recordId)
        return recordResponseBody
    }

    /**
     * 添加云解析 定时任务
     */
    @Path("/ddns/addScheduleDomainRecord")
    fun addScheduleDomainRecord(request: HttpServerRequest): Any {
        val domainName = request.getParam("domainName")
        val rr = request.getParam("rr")
        val type = request.getParam("type")
        val value = request.getParam("value")
        val ttl = request.getParam("ttl")
        val recordId = request.getParam("recordId")

        val domainRecordEntity = DomainRecordEntity()
        domainRecordEntity.recordId = recordId
        domainRecordEntity.domainName = domainName
        domainRecordEntity.rr = rr
        domainRecordEntity.type = type
        domainRecordEntity.value = value
        domainRecordEntity.updateTime = LocalDateTime.now()
        domainRecordEntity.createTime = LocalDateTime.now()
        domainRecordEntity.ttl = ttl.toInt()
        domainRecordEntity.upsert()
        return "ok"
    }


    @Path("/ddns/schedule")
    fun scheduleUpdateIpRecord(request: HttpServerRequest): String {
        this.schedule()
        return "设置完成"
    }


    fun schedule() {
        //查出数据库记录
        val findAll = DomainRecordEntity.findAll()
        findAll.forEach {
            if (it.type == "A") {
                updateIpRecord(it, ipAddrService.ipv4())
            }
            if (it.type == "AAAA") {
                updateIpRecord(it, ipAddrService.ipv6())
            }
        }
    }

    private fun updateIpRecord(it: DomainRecordEntity, ip: String) {
        if (ip == it.value) {
            log.info("地址未变，不更新DNS解析。 {}", objectMapper.writeValueAsString(it))
        } else {
            aliyunDnsService.updateDomainRecord(it.recordId!!, it.rr!!, it.type!!, ip, it.ttl!!)
            it.value = ip
            it.updateTime = LocalDateTime.now()
            it.upsert()
        }
    }


}

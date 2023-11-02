package com.github.blanexie.vxph.ddns.controller

import com.github.blanexie.vxph.core.R
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

    //设置定时任务
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
    fun findLocalIp(request: HttpServerRequest): R {
        val ipv6 = ipAddrService.ipv6()
        val ipv4 = ipAddrService.ipv4()
        return R.success().add("ipv4", ipv4).add("ipv6", ipv6)
    }

    /**
     * 查询域名的所有云解析记录
     */
    @Path("/ddns/findDomainRecords")
    fun findDomainRecords(request: HttpServerRequest): R {
        val domainName = request.getParam("domainName")
        val dbDomainRecords = DomainRecordEntity.findByDomain(domainName)
        val recordsResponseBody = aliyunDnsService.describeDomainRecords(domainName)
        return R.success().add("dbDomainRecords", dbDomainRecords)
            .add("aliyunDomainRecords", recordsResponseBody)
    }

    /**
     * 添加云解析记录
     */
    @Path("/ddns/addDomainRecord")
    fun addDomainRecord(request: HttpServerRequest): R {
        val domainName = request.getParam("domainName")
        val rr = request.getParam("rr")
        val type = request.getParam("type")
        val value = request.getParam("value")
        val ttl = request.getParam("ttl")
        val responseBody = aliyunDnsService.addDomainRecord(domainName, rr, type, value, ttl.toInt())
        return R.success().add("responseBody",responseBody)
    }

    /**
     * 删除云解析记录
     */
    @Path("/ddns/deleteDomainRecord")
    fun deleteDomainRecord(request: HttpServerRequest): R {
        val recordId = request.getParam("recordId")
        val recordResponseBody = aliyunDnsService.deleteDomainRecord(recordId)
        return R.success().add("responseBody",recordResponseBody)
    }

    /**
     * 添加云解析 定时任务
     */
    @Path("/ddns/addScheduleDomainRecord")
    fun addScheduleDomainRecord(request: HttpServerRequest): R {
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
        return R.success("ok")
    }


    @Path("/ddns/schedule")
    fun scheduleUpdateIpRecord(request: HttpServerRequest): R {
        this.schedule()
        return R.success()
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
            it.updateTime = LocalDateTime.now()
            it.upsert()
        } else {
            aliyunDnsService.updateDomainRecord(it.recordId!!, it.rr!!, it.type!!, ip, it.ttl!!)
            it.value = ip
            it.updateTime = LocalDateTime.now()
            it.upsert()
        }
    }


}

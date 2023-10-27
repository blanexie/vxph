package com.github.blanexie.vxph.ddns.controller

import cn.hutool.core.lang.Singleton
import com.github.blanexie.vxph.ddns.entity.DomainRecordEntity
import com.github.blanexie.vxph.ddns.service.AliyunDnsService
import com.github.blanexie.vxph.ddns.service.WebIpAddrServiceImpl
import com.github.blanexie.vxph.core.web.Path
import com.github.blanexie.vxph.core.objectMapper
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

@Path("/ddns")
class DDNSAction {

    private val log = LoggerFactory.getLogger(this::class.java)
    val aliyunDnsService = AliyunDnsService()
    val ipAddrService = WebIpAddrServiceImpl()

    /**
     * 查询域名的所有云解析记录
     */
    @Path("/findLocalIp")
    fun findLocalIp(request: HttpServerRequest): HttpServerResponse {
        val response = request.response()
        val ipv6 = ipAddrService.ipv6()
        val ipv4 = ipAddrService.ipv4()
        val valueAsString = objectMapper.writeValueAsString(mapOf("ipv6" to ipv6, "ipv4" to ipv4))

        response.send(valueAsString)
        return response
    }

    /**
     * 查询域名的所有云解析记录
     */
    @Path("/findDomainRecords")
    fun findDomainRecords(request: HttpServerRequest): HttpServerResponse {
        val domainName = request.getParam("domainName")
        val response = request.response()
        val dbDomainRecords = DomainRecordEntity.findByDomain(domainName)
        val recordsResponseBody = aliyunDnsService.describeDomainRecords(domainName)
        val valueAsString =
            objectMapper.writeValueAsString(mapOf("dbDomainRecords" to dbDomainRecords, "aliyunDomainRecords" to recordsResponseBody))
        response.send(valueAsString)
        return response
    }

    /**
     * 添加云解析记录
     */
    @Path("/addDomainRecord")
    fun addDomainRecord(request: HttpServerRequest): HttpServerResponse {
        val domainName = request.getParam("domainName")
        val rr = request.getParam("rr")
        val type = request.getParam("type")
        val value = request.getParam("value")
        val ttl = request.getParam("ttl")

        val response = request.response()
        val responseBody = aliyunDnsService.addDomainRecord(domainName, rr, type, value, ttl.toInt())
        response.send(objectMapper.writeValueAsString(responseBody))

        return response
    }

    /**
     * 删除云解析记录
     */
    @Path("/deleteDomainRecord")
    fun deleteDomainRecord(request: HttpServerRequest): HttpServerResponse {
        val recordId = request.getParam("recordId")

        val response = request.response()
        val recordResponseBody = aliyunDnsService.deleteDomainRecord(recordId)
        response.send(objectMapper.writeValueAsString(recordResponseBody))

        return response
    }

    /**
     * 添加云解析 定时任务
     */
    @Path("/addScheduleDomainRecord")
    fun addScheduleDomainRecord(request: HttpServerRequest): HttpServerResponse {
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

        val response = request.response()
        response.send("ok")
        return response
    }


    @Path("/schedule")
    fun scheduleUpdateIpRecord(request: HttpServerRequest): HttpServerResponse {
        this.schedule()
        val response = request.response()
        response.send("设置完成")
        return response
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

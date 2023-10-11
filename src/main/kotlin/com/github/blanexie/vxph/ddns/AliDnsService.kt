package com.github.blanexie.vxph.ddns

import com.aliyun.alidns20150109.Client
import com.aliyun.alidns20150109.models.DescribeSubDomainRecordsRequest
import com.aliyun.alidns20150109.models.DescribeSubDomainRecordsResponse
import com.aliyun.teaopenapi.models.Config

class AliDnsService(val accessKeyId: String, val accessKeySecret: String) {

    private val client: Client

    init {
        val config = Config() // 必填，您的 AccessKey ID
            .setAccessKeyId(accessKeyId) // 必填，您的 AccessKey Secret
            .setAccessKeySecret(accessKeySecret)
        config.endpoint = "alidns.cn-hangzhou.aliyuncs.com"
        this.client = Client(config);
    }

    fun getSubDomainParseList(subDomain: String, recordType: String): DescribeSubDomainRecordsResponse {
        val describeSubDomainRecordsRequest = DescribeSubDomainRecordsRequest()
            .setSubDomain(subDomain)
            .setType(recordType)
        return client.describeSubDomainRecords(describeSubDomainRecordsRequest)
    }

}
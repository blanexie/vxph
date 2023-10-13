package com.github.blanexie.vxph.ddns

import com.aliyun.auth.credentials.Credential
import com.aliyun.auth.credentials.provider.StaticCredentialProvider
import com.aliyun.sdk.service.alidns20150109.AsyncClient
import com.aliyun.sdk.service.alidns20150109.models.AddDomainRecordRequest
import com.aliyun.sdk.service.alidns20150109.models.DeleteDomainRecordRequest
import com.aliyun.sdk.service.alidns20150109.models.DeleteSubDomainRecordsRequest
import com.aliyun.sdk.service.alidns20150109.models.UpdateDomainRecordRequest
import darabonba.core.client.ClientOverrideConfiguration
import org.slf4j.LoggerFactory

class AliDnsService(val accessKeyId: String, val accessKeySecret: String) {

    private val client: AsyncClient
    private val log = LoggerFactory.getLogger(this::class.java)

    init {
        val provider = StaticCredentialProvider.create(
            Credential.builder() // Please ensure that the environment variables ALIBABA_CLOUD_ACCESS_KEY_ID and ALIBABA_CLOUD_ACCESS_KEY_SECRET are set.
                .accessKeyId(System.getenv(accessKeyId))
                .accessKeySecret(System.getenv(accessKeySecret)) //.securityToken(System.getenv("ALIBABA_CLOUD_SECURITY_TOKEN")) // use STS token
                .build()
        )
        client =
            AsyncClient.builder()  //.httpClient(httpClient) // Use the configured HttpClient, otherwise use the default HttpClient (Apache HttpClient)
                .credentialsProvider(provider) //.serviceConfiguration(Configuration.create()) // Service-level configuration
                // Client-level configuration rewrite, can set Endpoint, Http request parameters, etc.
                .overrideConfiguration(
                    ClientOverrideConfiguration.create() // Endpoint 请参考 https://api.aliyun.com/product/Alidns
                        .setEndpointOverride("alidns.cn-hangzhou.aliyuncs.com") //.setConnectTimeout(Duration.ofSeconds(30))
                )
                .build()
    }

    fun addDomainRecord(domainName: String, rr: String, type: String, value: String): String {
        val request = AddDomainRecordRequest.builder().domainName(domainName)
            .rr(rr).type(type).value(value).build()
        val responseFuture = client.addDomainRecord(request)

        val addDomainRecordResponse = responseFuture.get()
        val body = addDomainRecordResponse.body
        log.info("addDomainRecord  response:{}", body)
        return body.recordId
    }

    fun deleteDomainRecord(recordId: String) {
        val request = DeleteDomainRecordRequest.builder().recordId(recordId)
            .build()
        val responseFuture = client.deleteDomainRecord(request)
        responseFuture.handleAsync { t, u ->
            val body = t.body
            log.info("deleteDomainRecord  response:{}", body)
        }
    }

    fun deleteSubDomainRecords(domainName: String, rr: String, type: String) {
        val request = DeleteSubDomainRecordsRequest.builder()
            .domainName(domainName).rr(rr).type(type)
            .build()
        val responseFuture = client.deleteSubDomainRecords(request)
        responseFuture.handleAsync { t, u ->
            val body = t.body
            log.info("deleteSubDomainRecords  response:{}", body)
        }
    }

    fun updateDomainRecord(recordId: String, rr: String, type: String, value: String) {
        val request = UpdateDomainRecordRequest.builder()
            .recordId(recordId).rr(rr).type(type).value(value)
            .build()
        val responseFuture = client.updateDomainRecord(request)
        responseFuture.handleAsync { t, u ->
            val body = t.body
            log.info("updateDomainRecord  response:{}", body)
        }
    }


}
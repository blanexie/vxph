package com.github.blanexie.vxph.ddns.service

import com.aliyun.auth.credentials.Credential
import com.aliyun.auth.credentials.provider.StaticCredentialProvider
import com.aliyun.sdk.service.alidns20150109.AsyncClient
import com.aliyun.sdk.service.alidns20150109.models.*
import com.github.blanexie.vxph.ddns.Handler
import com.github.blanexie.vxph.ddns.accessKey
import com.github.blanexie.vxph.ddns.accessKeySecret
import darabonba.core.client.ClientOverrideConfiguration
import io.vertx.core.Promise
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer
import java.util.function.BiFunction


class AliyunDnsService {

    private val client: AsyncClient
    private val log = LoggerFactory.getLogger(this::class.java)

    init {
        val provider = StaticCredentialProvider.create(
            Credential.builder() // Please ensure that the environment variables ALIBABA_CLOUD_ACCESS_KEY_ID and ALIBABA_CLOUD_ACCESS_KEY_SECRET are set.
                .accessKeyId(accessKey)
                .accessKeySecret(accessKeySecret)  //.securityToken(System.getenv("ALIBABA_CLOUD_SECURITY_TOKEN")) // use STS token
                .build()
        )
        client =
            AsyncClient.builder()  //.httpClient(httpClient) // Use the configured HttpClient, otherwise use the default HttpClient (Apache HttpClient)
                .credentialsProvider(provider) //.serviceConfiguration(Configuration.create()) // Service-level configuration
                .overrideConfiguration(
                    ClientOverrideConfiguration.create() // Endpoint 请参考 https://api.aliyun.com/product/Alidns
                        .setEndpointOverride("alidns.cn-hangzhou.aliyuncs.com") //.setConnectTimeout(Duration.ofSeconds(30))
                ).build()
    }


    fun describeDomainRecords(
        domainName: String
    ): DescribeDomainRecordsResponseBody {
        val request = DescribeDomainRecordsRequest.builder().domainName(domainName)
            .pageSize(500).build()
        val responseFuture = client.describeDomainRecords(request)
        return responseFuture.get().body
    }

    fun addDomainRecord(
        domainName: String,
        rr: String,
        type: String,
        value: String,
        ttl: Int
    ): AddDomainRecordResponseBody {
        val request = AddDomainRecordRequest.builder().domainName(domainName)
            .TTL(ttl.toLong())
            .rr(rr).type(type).value(value).build()
        val responseFuture = client.addDomainRecord(request)
        return responseFuture.get().body

    }

    fun deleteDomainRecord(
        recordId: String
    ): DeleteDomainRecordResponseBody {
        val request = DeleteDomainRecordRequest.builder().recordId(recordId).build()
        val responseFuture = client.deleteDomainRecord(request)
        return responseFuture.get().body
    }

    fun deleteSubDomainRecords(
        domainName: String, rr: String, type: String,
        consumer: BiConsumer<DeleteSubDomainRecordsResponseBody, Throwable>
    ): DeleteSubDomainRecordsResponseBody {
        val request = DeleteSubDomainRecordsRequest.builder().domainName(domainName).rr(rr).type(type).build()
        val responseFuture = client.deleteSubDomainRecords(request)
        return responseFuture.get().body
    }

    fun updateDomainRecord(
        recordId: String, rr: String, type: String, value: String, ttl: Int,
        consumer: BiConsumer<UpdateDomainRecordResponseBody, Throwable>
    ) {
        val request = UpdateDomainRecordRequest.builder().recordId(recordId)
            .TTL(ttl.toLong())
            .rr(rr).type(type).value(value).build()
        val responseFuture = client.updateDomainRecord(request)
        responseFuture.handleAsync { t, u ->
            val body = t.body
            log.info("updateDomainRecord  response:{}", body)
            consumer.accept(body, u)
        }
    }

}


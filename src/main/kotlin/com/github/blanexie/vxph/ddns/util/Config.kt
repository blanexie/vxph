package com.github.blanexie.vxph.ddns.util

import org.springframework.stereotype.Component
import com.aliyun.alidns20150109.Client
import com.aliyun.teaopenapi.models.Config
import com.github.blanexie.vxph.common.getProperty
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

@Component
class Config(
    @Value("\${aliyun.dns.accessKeyId}")
    val accessKeyId: String,
    @Value("\${aliyun.dns.accessKeySecret}")
    val accessKeySecret: String
) {

    @Bean
    fun client(): Client {
        val config = Config()
            .setAccessKeyId(accessKeyId)
            .setAccessKeySecret(accessKeySecret)
            .setEndpoint("alidns.cn-hangzhou.aliyuncs.com")

        return Client(config)
    }


}
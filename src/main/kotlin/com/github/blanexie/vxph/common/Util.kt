package com.github.blanexie.vxph.common

import cn.hutool.cache.CacheUtil
import cn.hutool.captcha.AbstractCaptcha
import com.dampcake.bencode.Bencode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.blanexie.vxph.context
import org.springframework.core.env.Environment


fun getProperty(key: String): String? {
    val bean = context!!.getBean(Environment::class.java)
    return bean.getProperty(key)
}


//五分钟定时缓存， 缓存验证码
val timeCaptchaCache = CacheUtil.newTimedCache<String, AbstractCaptcha>(5 * 60 * 1000)



val bencode = Bencode(true)

val objectMapper = jacksonObjectMapper()
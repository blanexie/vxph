package com.github.blanexie.vxph.common

import com.dampcake.bencode.Bencode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.blanexie.vxph.context
import org.springframework.core.env.Environment


fun <T> getBean(clazz: Class<T>): T {
    return context!!.getBean(clazz)
}

fun getProperty(key: String): String? {
    val bean = context!!.getBean(Environment::class.java)
    return bean.getProperty(key)
}

val bencode = Bencode(true)

val objectMapper = jacksonObjectMapper()
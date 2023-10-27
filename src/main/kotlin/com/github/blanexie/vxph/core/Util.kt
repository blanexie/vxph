package com.github.blanexie.vxph.core

import cn.hutool.core.convert.Convert
import cn.hutool.core.util.ClassUtil
import cn.hutool.setting.Setting
import cn.hutool.setting.SettingUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.github.blanexie.vxph.core.event.Event
import com.github.blanexie.vxph.core.event.VerticleLoadCompleteEventType
import io.vertx.core.Future
import io.vertx.core.Vertx
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap

private val log = LoggerFactory.getLogger("Util")

//加载配置文件
val setting: Setting = SettingUtil.get(System.getProperty("properties.path") ?: "vxph.properties")
val contextMap = ConcurrentHashMap<String, Any>()
val objectMapper: ObjectMapper
    get() {
        val s = contextMap.computeIfAbsent("objectMapper") {
            val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val objectMapper = ObjectMapper()
            val module = SimpleModule()
            module.addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(dateTimeFormatter))
            module.addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(dateTimeFormatter))
            objectMapper.registerModules(module)
            objectMapper
        }
        return s as ObjectMapper
    }


fun loadAnnotationClass(packageName: String, vertx: Vertx) {
    contextMap["vertx"] = vertx
    val verticles = ClassUtil.scanPackageByAnnotation(packageName, Verticle::class.java)
    val futures = verticles.map {
        log.info("deployVerticle {}", it.name)
        vertx.deployVerticle(it.name)
    }.toList()

    Future.all(futures).onSuccess {
        log.info("deployVerticle end, publish event, packagePath:{}", packageName)
        Event(VerticleLoadCompleteEventType, packageName).publish(vertx)
    }
}

inline fun <reified T> getProperty(key: String, defaultValue: T): T {
    val property = System.getProperty(key) ?: setting[key]
    return Convert.convert(T::class.java, property ?: defaultValue)
}

inline fun <reified T> getProperty(key: String): T? {
    val property = System.getProperty(key) ?: setting[key]
    return Convert.convert(T::class.java, property)
}

inline fun <reified T> ConcurrentHashMap<String, Any>.getVal(key: String): T? {
    return this[key] as T
}

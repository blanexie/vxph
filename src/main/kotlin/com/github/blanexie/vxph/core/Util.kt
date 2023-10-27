package com.github.blanexie.vxph.core

import cn.hutool.core.convert.Convert
import cn.hutool.core.util.ClassUtil
import cn.hutool.setting.Setting
import cn.hutool.setting.SettingUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.github.blanexie.vxph.core.event.Event
import com.github.blanexie.vxph.core.event.VerticleLoadCompleteEventType
import io.vertx.core.Future
import io.vertx.core.Vertx
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap


//加载配置文件
val setting: Setting = SettingUtil.get(System.getProperty("properties.path") ?: "vxph.properties")
val contextMap = ConcurrentHashMap<String, Any>()
val objectMapper: ObjectMapper
    get() {
        val s = contextMap.computeIfAbsent("objectMapper") {
            val objectMapper = ObjectMapper()
            val module = SimpleModule()
            module.addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer.INSTANCE)
            objectMapper
        }
        return s as ObjectMapper
    }


fun loadAnnotationClass(packageName: String, vertxV: Vertx) {

    val vertx = contextMap.computeIfAbsent("vertx") {
        vertxV
    } as Vertx

    val verticleClasses = ClassUtil.scanPackageByAnnotation(packageName, Verticle::class.java)
    val futures = verticleClasses.map {
        vertx.deployVerticle(it.name)
    }.toList()

    Future.all(futures).onSuccess {
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

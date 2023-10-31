package com.github.blanexie.vxph.core

import cn.hutool.core.convert.Convert
import cn.hutool.core.util.ClassUtil
import cn.hutool.core.util.ReflectUtil
import cn.hutool.setting.Setting
import cn.hutool.setting.SettingUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.github.blanexie.vxph.core.event.Event
import com.github.blanexie.vxph.core.event.VerticleLoadCompleteEventType
import com.github.blanexie.vxph.core.web.Filter
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap

private val log = LoggerFactory.getLogger("Util")

//加载配置文件
val setting: Setting = SettingUtil.get(System.getProperty("properties.path") ?: "vxph.properties")
val port = getProperty("vxph.http.server.port", 8016)
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
    //扫描Web请求的拦截器
    val filterClazz = ClassUtil.scanPackageByAnnotation(packageName, Filter::class.java)
    contextMap.computeIfAbsent("httpFilters") {
        filterClazz.map { it.getDeclaredConstructor().newInstance() }.toList()
    }
    //扫描Verticle
    val verticleClazz = ClassUtil.scanPackageByAnnotation(packageName, Verticle::class.java)
    val futures = verticleClazz.map {
        log.info("deploy Verticle {}", it.name)
        vertx.deployVerticle(it.name)
    }.toList()

    Future.all(futures)
        .onFailure {
            log.error("verticle start error ", it)
        }
        .onSuccess {
            contextMap["router"]?.let {
                val router = it as Router
                val httpServer = vertx.createHttpServer()
                httpServer.requestHandler(router).listen(port)
                log.info("httpServer enable, listen port:{}", port)
            }
            log.info("deployVerticle end, publish event, packagePath:{}", packageName)
            Event(VerticleLoadCompleteEventType, packageName).publish(vertx)
        }
}

inline fun <reified T> getProperty(key: String, defaultValue: T): T {
    val property = System.getProperty(key) ?: setting[key]?.trim()
    return Convert.convert(T::class.java, property ?: defaultValue)
}

inline fun <reified T> getProperty(key: String): T? {
    val property = System.getProperty(key) ?: setting[key]?.trim()
    return Convert.convert(T::class.java, property)
}

inline fun <reified T> ConcurrentHashMap<String, Any>.getVal(key: String): T? {
    return this[key] as T
}

inline fun <reified T> ConcurrentHashMap<String, Any>.getIfAbsent(key: String, mappingFunction: java.util.function.Function<String, out T>): T {
    val computeIfAbsent = this.computeIfAbsent(key, mappingFunction)
    return computeIfAbsent as T
}


fun RoutingContext.respFail(code: Int, e: Throwable) {
    val response = this.response()
    response.statusCode = code
    response.putHeader("content-type", "text/plain; charset=utf-8 ")
    response.send(e.message)
}

class R (
    val code: Int,
    val error: String,
    val data: HashMap<String, Any>
) {

    fun add(key: String, v: Any): R  {
        this.data[key] = v
        return this
    }

    companion object {
        fun success(data: HashMap<String, Any>): R {
            return R(code = 200, error = "", data = data)
        }

        fun success(): R {
            return R(code = 200, error = "", data = hashMapOf())
        }

        fun fail(code: Int, error: String): R {
            return R(code = code, error = error, data = hashMapOf())
        }
    }
}



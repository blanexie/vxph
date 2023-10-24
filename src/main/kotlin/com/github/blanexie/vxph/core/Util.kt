package com.github.blanexie.vxph.core

import cn.hutool.core.lang.Singleton
import cn.hutool.core.util.ClassUtil
import cn.hutool.db.DbUtil
import cn.hutool.log.level.Level
import cn.hutool.setting.Setting
import cn.hutool.setting.SettingUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.vertx.core.Vertx

//加载配置文件
val setting: Setting
    get() {
        val property = System.getProperty("properties.path")
        return SettingUtil.get(property ?: "vxph.properties")
    }


val port = setting.getInt("vxph.http.server.port", 8061)!!

val sqlitePath: String
    get() {
        val property = System.getProperty("sqlite.path")
        return if (property == null) {
            setting.getStr("vxph.database.jdbc.url")
        } else {
            "jdbc:sqlite:$property"
        }
    }

val objectMapper: ObjectMapper = ObjectMapper()

fun hikariDataSource(): HikariDataSource? {
    val enable = setting.getBool("vxph.database.sqlite.enable", false)
    return if (enable) {
        Singleton.get("hikariDataSource") {
            DbUtil.setShowSqlGlobal(true, true, true, Level.INFO)
            val hikariConfig = HikariConfig()
            hikariConfig.jdbcUrl = sqlitePath
            hikariConfig.schema = "main"
            hikariConfig.username = setting.getStr("vxph.database.jdbc.user")
            hikariConfig.password = setting.getStr("vxph.database.jdbc.password")
            hikariConfig.maximumPoolSize = setting.getInt("vxph.database.jdbc.maxPoolSize", 8)
            hikariConfig.driverClassName = setting.getStr("vxph.database.jdbc.driverClassName")
            HikariDataSource(hikariConfig)
        }
    } else {
        null
    }
}


val annotationSet = hashSetOf<Class<*>>()
fun loadAnnotationClass(packageName: String, vertx: Vertx) {
    val pathClasses =
        ClassUtil.scanPackageByAnnotation(packageName, Path::class.java)
    annotationSet.addAll(pathClasses)
    val verticleClasses = ClassUtil.scanPackageByAnnotation(packageName, Verticle::class.java)
    verticleClasses.forEach {
        vertx.deployVerticle(it.name)
    }
}



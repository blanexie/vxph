package com.github.blanexie.vxph.utils

import cn.hutool.core.lang.Singleton
import cn.hutool.db.DbUtil
import cn.hutool.log.level.Level
import cn.hutool.setting.Setting
import cn.hutool.setting.SettingUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

//加载配置文件
val setting: Setting = SettingUtil.get("vxph.properties")

val port = setting.getInt("vxph.http.server.port", 8061)

fun hikariDataSource(): HikariDataSource {
    return Singleton.get("hikariDataSource") {
        DbUtil.setShowSqlGlobal(true, true, true, Level.INFO)

        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl = setting.getStr("vxph.database.jdbc.url")
        hikariConfig.username = setting.getStr("vxph.database.jdbc.user")
        hikariConfig.password = setting.getStr("vxph.database.jdbc.password")
        hikariConfig.maximumPoolSize = setting.getInt("vxph.database.jdbc.maxPoolSize", 8)
        hikariConfig.driverClassName = setting.getStr("vxph.database.jdbc.driverClassName")

        HikariDataSource(hikariConfig)
    }
}


val objectMapper: ObjectMapper = ObjectMapper()

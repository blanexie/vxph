package com.github.blanexie.vxph.tracker

import cn.hutool.core.lang.Singleton
import cn.hutool.core.util.ClassUtil
import cn.hutool.db.DbUtil
import cn.hutool.db.dialect.impl.Sqlite3Dialect
import cn.hutool.log.level.Level
import cn.hutool.setting.Setting
import cn.hutool.setting.SettingUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.vertx.core.json.JsonObject
import io.vertx.jdbcclient.JDBCPool
import java.util.concurrent.atomic.AtomicReference


const val announceUrl = "/announce"

//加载配置文件

val setting: Setting = SettingUtil.get("vxph.properties")
val dialect = Sqlite3Dialect()

val objectMapper: ObjectMapper = ObjectMapper()


fun hikariDataSource(): HikariDataSource {
    return Singleton.get("hikariDataSource") {

        DbUtil.setShowSqlGlobal(true,true,true,Level.INFO)

        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl = setting.getStr("vxph.database.jdbc.url")
        hikariConfig.username = setting.getStr("vxph.database.jdbc.user")
        hikariConfig.password = setting.getStr("vxph.database.jdbc.password")
        hikariConfig.maximumPoolSize = setting.getInt("vxph.database.jdbc.maxPoolSize", 8)
        hikariConfig.driverClassName = setting.getStr("vxph.database.jdbc.driverClassName")
        HikariDataSource(hikariConfig)
    }
}





class Util {


}


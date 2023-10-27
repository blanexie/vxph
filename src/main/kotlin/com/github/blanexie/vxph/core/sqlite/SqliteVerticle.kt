package com.github.blanexie.vxph.core.sqlite

import cn.hutool.core.io.FileUtil
import cn.hutool.core.util.CharsetUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.db.Db
import cn.hutool.db.DbUtil
import cn.hutool.db.dialect.impl.Sqlite3Dialect
import cn.hutool.log.level.Level
import com.github.blanexie.vxph.core.Verticle
import com.github.blanexie.vxph.core.contextMap
import com.github.blanexie.vxph.core.getProperty
import com.github.blanexie.vxph.core.getVal
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.slf4j.LoggerFactory
import java.io.File


@Verticle
class SqliteVerticle : CoroutineVerticle() {
    private val log = LoggerFactory.getLogger(this::class.java)

    override suspend fun start() {
        //加载数据库
        log.info("SqliteVerticle deploy start ")
        val hikariDataSource = hikariDataSource()
        hikariDataSource?.let {
            contextMap.put("hikariDataSource", hikariDataSource)
        }
        initDDLSQL()
        log.info("SqliteVerticle start fun end")
    }

    private fun hikariDataSource(): HikariDataSource? {
        val enable: Boolean = getProperty("vxph.database.sqlite.enable", false)
        return if (enable) {
            DbUtil.setShowSqlGlobal(true, true, true, Level.INFO)
            val hikariConfig = HikariConfig()
            hikariConfig.jdbcUrl = getProperty("vxph.database.jdbc.url")
            hikariConfig.username = getProperty("vxph.database.jdbc.user")
            hikariConfig.password = getProperty("vxph.database.jdbc.password")
            hikariConfig.maximumPoolSize = getProperty("vxph.database.jdbc.maxPoolSize", 8)
            hikariConfig.driverClassName = getProperty("vxph.database.jdbc.driverClassName")
            HikariDataSource(hikariConfig)
        } else {
            null
        }
    }

    private suspend fun initDDLSQL() {
        //初始化数据库
        log.info("SqliteVerticle initDDLSQL start")
        val property = getProperty<String>("vxph.database.sqlite.ddl")
        property?.let { p ->
            val hikariDataSource: HikariDataSource? = contextMap.getVal("hikariDataSource")
            hikariDataSource?.let {
                val ddlSql = FileUtil.readString(File(p), CharsetUtil.CHARSET_UTF_8)
                val sqls = ddlSql.split(";")
                for (sql in sqls) {
                    val trim = StrUtil.trim(sql)
                    if (StrUtil.isNotBlank(trim)) {
                        DbUtil.use(it).execute(trim)
                    }
                }
            }
        }
    }
}


fun hikariDb(): Db {
    val hikariDataSource: HikariDataSource = contextMap.getVal("hikariDataSource")!!
    return DbUtil.use(hikariDataSource, Sqlite3Dialect())
}
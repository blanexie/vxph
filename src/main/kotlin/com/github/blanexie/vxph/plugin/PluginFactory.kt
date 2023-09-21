package com.github.blanexie.vxph.plugin

import cn.hutool.setting.SettingUtil
import com.github.blanexie.vxph.core.AbstractVerticle
import com.github.blanexie.vxph.core.objectMapper
import com.github.blanexie.vxph.plugin.inbund.CronSchedulePlugin
import com.github.blanexie.vxph.plugin.inbund.HttpServerPlugin
import com.github.blanexie.vxph.plugin.outbund.EmailPlugin
import com.github.blanexie.vxph.plugin.outbund.HttpClientPlugin
import com.github.blanexie.vxph.plugin.outbund.JdbcPlugin

class PluginFactory {

    fun build(row: Map<*, *>): AbstractVerticle {
        val type = row["type"]
        val argsMap = objectMapper.readValue(row["args"] as String, Map::class.java)
        return when (type) {
            "httpServer" -> buildHttpServer(argsMap)
            "cronSchedule" -> buildCronSchedule(argsMap)
            "email" -> buildEmail(argsMap)
            "httpClient" -> buildHttpClient(argsMap)
            "jdbcPlugin" -> buildJdbcPlugin(argsMap)
            else -> throw Error("type：$type 是未定义的插件类型， 无法加载")
        }
    }

    private fun buildJdbcPlugin(argsMap: Map<*, *>): JdbcPlugin {
        val jdbcUrl = argsMap["jdbcUrl"] as String
        val username = argsMap["username"] as String
        val password = argsMap["password"] as String
        val maxPoolSize = argsMap["maxPoolSize"] as Int
        return JdbcPlugin(jdbcUrl, username, password, maxPoolSize)
    }

    private fun buildHttpClient(argsMap: Map<*, *>): HttpClientPlugin {
        val name = argsMap["name"] as String
        return HttpClientPlugin(name)
    }

    private fun buildEmail(argsMap: Map<*, *>): EmailPlugin {
        val hostName = argsMap["hostName"] as String
        val port = argsMap["port"] as Int
        val userName = argsMap["userName"] as String
        val password = argsMap["password"] as String
        return EmailPlugin(hostName, port, userName, password)
    }

    private fun buildCronSchedule(argsMap: Map<*, *>): CronSchedulePlugin {
        val cron = argsMap["cron"] as String
        return CronSchedulePlugin(cron)
    }

    private fun buildHttpServer(argsMap: Map<*, *>): HttpServerPlugin {
        val path = argsMap["path"] as String
        val port = argsMap["port"] as Int
        return HttpServerPlugin(path, port)
    }


     fun buildCoreJdbcPlugin(): JdbcPlugin {
        val receiver = getCoreJdbcPluginTopic();
        val jdbcUrl = setting.getStr("vxph.database.jdbc.url")
        val username = setting.getStr("vxph.database.username", "")
        val password = setting.getStr("vxph.database.password", "")
        val maxPoolSize = setting.getInt("vxph.database.maxPoolSize", 16)
        return JdbcPlugin(jdbcUrl, username, password, maxPoolSize, true)
    }

}


private val setting = SettingUtil.get("vxph.properties")

val pluginFactory = PluginFactory()
fun getCoreJdbcPluginTopic(): String {
    val jdbcUrl = setting.getStr("vxph.database.jdbc.url")
    val username = setting.getStr("vxph.database.username", "")
    return "jdbcPlugin:$jdbcUrl:$username"
}



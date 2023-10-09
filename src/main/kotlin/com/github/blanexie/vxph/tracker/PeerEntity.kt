package com.github.blanexie.vxph.tracker

import cn.hutool.core.bean.BeanUtil
import cn.hutool.core.map.MapUtil
import cn.hutool.core.util.StrUtil
import io.vertx.core.net.SocketAddress
import io.vertx.jdbcclient.JDBCPool
import io.vertx.sqlclient.templates.SqlTemplate
import java.time.LocalDateTime

class PeerEntity(
    var passkey: String,
    var peerId: String,
    var infoHash: ByteArray,
    var remoteAddress: String,
    var port: Int,
    var downloaded: Long,
    var left: Long,
    var uploaded: Long,
    var event: String,
    var createTime: LocalDateTime,
    var updateTime: LocalDateTime,
    var status: Int,
) {


    init {
     val insertOrUpdateSql  = "";
        val beanToMap = BeanUtil.beanToMap(this)
        beanToMap.forEach { k, v ->



        }

    }


    fun insertOrUpdate(jdbcPool: JDBCPool) {
        val sql =
            """
               INSERT INTO peer(passkey,peerId,infoHash,remoteAddress,port,downloaded,left,uploaded,event,createTime, updateTime,status) 
               VALUES(#{passkey}, #{peerId}, #{infoHash}, #{remoteAddress},#{port},#{downloaded},#{left},#{uploaded},#{event},#{createTime},#{updateTime},#{status}) 
               ON CONFLICT(passkey,peerId,infoHash) DO UPDATE
               SET passkey=excluded.passkey, peerId=excluded.peerId, infoHash=excluded.infoHash , remoteAddress=excluded.remoteAddress,
               port=excluded.port , downloaded=excluded.downloaded , uploaded=excluded.uploaded ,createTime=excluded.createTime ,
               updateTime=excluded.updateTime ,status=excluded.status 
            """



      //  StrUtil.toUnderlineCase()
//

        SqlTemplate.forUpdate(jdbcPool, sql)
           // .execute(beanToMap)


    }


}
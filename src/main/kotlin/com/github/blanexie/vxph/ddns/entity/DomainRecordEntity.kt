package com.github.blanexie.vxph.ddns.entity

import cn.hutool.core.bean.BeanUtil
import cn.hutool.db.Db
import cn.hutool.db.Entity
import com.github.blanexie.vxph.tracker.toEntity
import com.github.blanexie.vxph.utils.hikariDataSource
import java.time.LocalDateTime


/**
 *Status	string
 * 当前的解析记录状态。
 *
 * Enable
 * Type	string
 * 记录类型。
 *
 * MX
 * Remark	string
 * 备注。
 *
 * 备注
 * TTL	long
 * 缓存时间设置。单位：秒。
 *
 * 600
 * RecordId	string
 * 解析记录ID。
 *
 * 9999985
 * Priority	long
 * mx记录的优先级。
 *
 * 5
 * RR	string
 * 主机记录。
 *
 * www
 * DomainName	string
 * 域名名称。
 *
 * example.com
 * Weight	integer
 * 负载均衡权重。
 *
 * 2
 * Value	string
 * 记录值。
 *
 * mail1.hichina.com
 * Line	string
 * 解析线路。
 *
 * default
 * Locked	boolean
 * 当前解析记录锁定状态。
 *
 * false
 * CreateTimestamp	long
 * 创建时间（时间戳）。
 *
 * 1666501957000
 * UpdateTimestamp	long
 * 更新时间（时间戳）。
 *
 * 1676872961000
 */
class DomainRecordEntity {

    var recordId: String? = null
    var type: String? = null
    var rr: String? = null
    var value: String? = null
    var domainName: String? = null
    var ttl: Int? = 600
    var remark: String? = null


    var createTime: LocalDateTime? = null
    var updateTime: LocalDateTime? = null
    var status: Int = 0

    fun upsert() {
        val entity = BeanUtil.beanToMap(this).toEntity("DomainRecord")
        Db.use(hikariDataSource()).upsert(entity, "recordId")
    }

    companion object {

        fun findAll(): List<DomainRecordEntity> {
            return Db.use(hikariDataSource())
                .findAll(Entity.create("DomainRecord"), DomainRecordEntity::class.java)
        }

    }


}
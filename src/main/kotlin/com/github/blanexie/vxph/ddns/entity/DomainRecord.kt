package com.github.blanexie.vxph.ddns.entity

import com.github.blanexie.vxph.common.BaseEntity
import jakarta.persistence.*

@Entity
data class DomainRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long?,
    @Column(unique = true)
    var recordId: String?,
    @Column(nullable = false)
    var domainName: String,
    @Column(nullable = false)
    var type: String,
    @Column(nullable = false)
    var rr: String,
    @Column(nullable = false)
    var value: String,
    @Column
    var ttl: Int,
    @Column
    var remark: String,
) : BaseEntity()
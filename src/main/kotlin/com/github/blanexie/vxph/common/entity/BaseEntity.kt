package com.github.blanexie.vxph.common.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Version
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
open class BaseEntity(
    @Version
    @Column(nullable = false)
    var versionNo: Int = 0,

    @Column(nullable = false, updatable = false)
    @CreatedDate
    var createTime: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = false)
    @LastModifiedDate
    var updateTime: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = false, name = "v_status")
    var status: Int = 0,
)
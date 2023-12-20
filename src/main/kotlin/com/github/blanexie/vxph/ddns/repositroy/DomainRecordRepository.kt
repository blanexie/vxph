package com.github.blanexie.vxph.ddns.repositroy

import com.github.blanexie.vxph.ddns.entity.DomainRecord
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor

interface DomainRecordRepository : CrudRepository<DomainRecord, Long>, QueryByExampleExecutor<DomainRecord> {

    fun findByRecordId(recordId: String): DomainRecord?
    fun deleteByRecordId(recordId: String)

}
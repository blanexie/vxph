package com.github.blanexie.vxph.ddns.service

import com.aliyun.alidns20150109.Client
import com.aliyun.alidns20150109.models.*
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.exception.VxphException
import com.github.blanexie.vxph.ddns.entity.DomainRecord
import org.springframework.stereotype.Service


/**
 * 与阿里云相关的操作
 */
@Service
class AliyunDdnsService(
    val client: Client,
) {

    /**
     * 返回recordId
     */
    fun addRecord(domainRecord: DomainRecord): String {
        if (domainRecord.recordId != null) {
            throw VxphException(SysCode.RecordIdExist)
        }
        val addDomainRecordRequest = AddDomainRecordRequest()
        addDomainRecordRequest.ttl = domainRecord.ttl.toLong()
        addDomainRecordRequest.rr = domainRecord.rr
        addDomainRecordRequest.type = domainRecord.type
        addDomainRecordRequest.value = domainRecord.value
        addDomainRecordRequest.domainName = domainRecord.domainName
        val addDomainRecord = client.addDomainRecord(addDomainRecordRequest)
        if (addDomainRecord.statusCode != 200) {
            throw VxphException(SysCode.AliyunClientError)
        }
        return addDomainRecord.body.recordId
    }

    fun deleteRecord(recordId: String) {
        val deleteDomainRecordRequest = DeleteDomainRecordRequest()
        deleteDomainRecordRequest.recordId = recordId
        val deleteDomainRecord = client.deleteDomainRecord(deleteDomainRecordRequest)
        if (deleteDomainRecord.statusCode != 200) {
            throw VxphException(SysCode.AliyunClientError, deleteDomainRecord.statusCode.toString())
        }
    }


    fun updateRecord(domainRecord: DomainRecord) {
        val updateDomainRecordRequest = UpdateDomainRecordRequest()
        updateDomainRecordRequest.ttl = domainRecord.ttl.toLong()
        updateDomainRecordRequest.rr = domainRecord.rr
        updateDomainRecordRequest.type = domainRecord.type
        updateDomainRecordRequest.value = domainRecord.value
        updateDomainRecordRequest.recordId = domainRecord.recordId
        val updateDomainRecord = client.updateDomainRecord(updateDomainRecordRequest)
        if (updateDomainRecord.statusCode != 200) {
            throw VxphException(SysCode.AliyunClientError, updateDomainRecord.statusCode.toString())
        }
    }


    fun findRecord(recordId: String): DescribeDomainRecordInfoResponseBody {
        val describeDomainRecordInfo = DescribeDomainRecordInfoRequest()
        describeDomainRecordInfo.recordId = recordId

        val describeDomainRecords = client.describeDomainRecordInfo(describeDomainRecordInfo)
        return describeDomainRecords.body
    }

}
package com.github.blanexie.vxph.torrent.entity

import cn.hutool.core.util.StrUtil
import com.github.blanexie.vxph.common.entity.BaseEntity
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.exception.VxphException
import com.github.blanexie.vxph.torrent.controller.dto.AnnounceReq
import com.github.blanexie.vxph.torrent.controller.dto.PeerResp
import com.github.blanexie.vxph.torrent.util.IpType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class Peer(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long?,
    var infoHash: String,
    @Column(unique = true)
    var passKey: String,
    var peerId: String?,
    var ipv4: String?,
    var ipv6: String?,
    var port: Int?,
    var downloaded: Long,
    @Column(name = "`left`")
    var left: Long,
    var uploaded: Long,
    var event: String,
    var uploadTime: LocalDateTime,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "torrent_id")
    var torrent: Torrent,
    var owner: Long
) : BaseEntity() {


    fun refresh(announceReq: AnnounceReq) {
        this.uploadTime = LocalDateTime.now()
        this.peerId = announceReq.peerId

        //判断是ipv4 还是ipv6
        if (announceReq.remoteAddr.contains(".")) {
            this.ipv4 = announceReq.remoteAddr
            this.ipv6 = null
        } else if (announceReq.remoteAddr.contains(":")) {
            this.ipv4 = null
            this.ipv6 = announceReq.remoteAddr
        } else {
            throw VxphException(SysCode.RemoteIpError)
        }

        this.port = announceReq.remotePort
        this.left = announceReq.left
        this.downloaded = announceReq.downloaded
        this.uploaded = announceReq.uploaded
        this.event = announceReq.event
    }


    fun toPeerResps(): List<PeerResp> {
        val result = mutableListOf<PeerResp>()
        if (StrUtil.isNotBlank(ipv6)) {
            result.add(PeerResp(this.peerId!!, this.ipv6!!, this.port!!, IpType.IPV6))
        }
        if (StrUtil.isNotBlank(ipv4)) {
            result.add(PeerResp(this.peerId!!, this.ipv4!!, this.port!!, IpType.IPV4))
        }
        return result
    }


}
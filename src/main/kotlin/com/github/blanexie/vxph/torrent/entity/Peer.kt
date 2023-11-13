package com.github.blanexie.vxph.torrent.entity

import com.github.blanexie.vxph.common.BaseEntity
import com.github.blanexie.vxph.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class Peer(
    @Id
    var id: Long?,
    var infoHash: String,
    @Column(unique = true)
    var passKey: String,
    var peerId: String?,
    var ipv4: String?,
    var ipv6: String?,
    var port: Int?,
    var downloaded: Long,
    var left: Long,
    var uploaded: Long,
    var event: String,
    var uploadTime: LocalDateTime,


    @ManyToOne
    var torrent: Torrent,
    @OneToOne
    var user: User

) : BaseEntity() {
}
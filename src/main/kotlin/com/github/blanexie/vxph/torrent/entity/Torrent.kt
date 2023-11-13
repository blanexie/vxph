package com.github.blanexie.vxph.torrent.entity

import com.github.blanexie.vxph.common.BaseEntity
import com.github.blanexie.vxph.user.entity.User
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import java.time.LocalDateTime


@Entity
data class Torrent(
    @Id
    var infoHash: String,
    var name: String,
    var length: Long,
    var comment: String,
    var files: String,
    var creationDate: LocalDateTime,
    var pieceLength: Long,
    var publisher: String,
    var singleFile: Boolean,

    @OneToMany
    var peer: List<Peer>,
    @OneToOne
    var creater: User

) : BaseEntity()
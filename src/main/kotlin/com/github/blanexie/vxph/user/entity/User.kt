package com.github.blanexie.vxph.user.entity

import cn.hutool.crypto.digest.DigestUtil
import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.blanexie.vxph.common.BaseEntity
import com.github.blanexie.vxph.common.getProperty
import com.github.blanexie.vxph.torrent.announceIntervalMinute
import com.github.blanexie.vxph.user.LoginTimeExpireMS
import jakarta.persistence.*
import org.slf4j.LoggerFactory
import org.springframework.util.DigestUtils


private val log = LoggerFactory.getLogger(User::class.java)

@Entity
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long?,
    @Column(unique = true, nullable = false)
    var name: String,
    @Column(unique = true, nullable = false)
    var email: String,
    @JsonIgnore
    @Column(nullable = false)
    var password: String,
    @Column(nullable = false)
    var sex: String,
    @OneToOne
    var account: Account,
    @OneToMany
    var roles: List<Role>,
) : BaseEntity() {

    fun checkPwd(pwdSha256: String, time: Long): Boolean {


        val sha256Hex = DigestUtil.sha256Hex("$name$password$time")
        log.info("login userId:{} name:{} ,passowrd:{} , pwdSha256Hex:{}", id, name, password, sha256Hex)
        return sha256Hex == pwdSha256
    }

}
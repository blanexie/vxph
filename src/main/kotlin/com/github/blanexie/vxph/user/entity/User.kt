package com.github.blanexie.vxph.user.entity

import cn.hutool.crypto.digest.DigestUtil
import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.blanexie.vxph.common.entity.BaseEntity
import jakarta.persistence.*
import org.slf4j.LoggerFactory


private val log = LoggerFactory.getLogger(User::class.java)

@Entity
@Table(name = "v_user")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long?,
    @Column(unique = true, name = "`name`")
    var name: String,
    @Column(unique = true, name = "`email`")
    var email: String,
    @JsonIgnore
    @Column(name = "`password`")
    var password: String,
    @Column(name = "`sex`")
    var sex: Int,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "`role`")
    var role: Role,
) : BaseEntity() {

    fun checkPwd(pwdSha256: String, time: Long): Boolean {
        val sha256Hex = DigestUtil.sha256Hex("$name$password$time")
        log.info("login userId:{} name:{} ,passowrd:{} , pwdSha256Hex:{}", id, name, password, sha256Hex)
        return sha256Hex == pwdSha256
    }

}
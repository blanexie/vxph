package com.github.blanexie.vxph.user.entity

import cn.hutool.crypto.digest.DigestUtil
import com.github.blanexie.vxph.common.BaseEntity
import com.github.blanexie.vxph.common.getProperty
import jakarta.persistence.*
import org.springframework.util.DigestUtils


@Entity
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long?,
    @Column(unique = true, nullable = false)
    var name: String,
    @Column(unique = true, nullable = false)
    var email: String,
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
        return sha256Hex == pwdSha256
    }

}
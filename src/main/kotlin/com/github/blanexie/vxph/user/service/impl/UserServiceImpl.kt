package com.github.blanexie.vxph.user.service.impl

import cn.hutool.cache.CacheUtil
import com.github.blanexie.vxph.account.entity.Account
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.exception.VxphException
import com.github.blanexie.vxph.user.LoginTimeExpireMS
import com.github.blanexie.vxph.user.dto.RegisterReq
import com.github.blanexie.vxph.user.entity.Role
import com.github.blanexie.vxph.user.entity.User
import com.github.blanexie.vxph.user.repository.UserRepository
import com.github.blanexie.vxph.user.service.UserService
import jakarta.annotation.Resource
import org.springframework.stereotype.Service


@Service
class UserServiceImpl(@Resource val userRepository: UserRepository) : UserService {

    private val userCache = CacheUtil.newLRUCache<Long, User>(100, 30 * 60 * 1000)

    override fun login(name: String, pwdSha256: String, time: Long): User? {
        val betweenMS = System.currentTimeMillis() - time
        if (betweenMS > LoginTimeExpireMS) {
            return null
        }
        val user = userRepository.findByName(name)
        if (user != null && user.checkPwd(pwdSha256, time)) {
            return user
        }
        return null
    }

    override fun findById(userId: Long): User? {
        return userCache.get(userId) {
            userRepository.findById(userId).orElse(null)
        }
    }

    override fun saveUser(registerReq: RegisterReq, account: Account, role: Role): User {
        var user = userRepository.findByName(registerReq.name)
        if (user != null) {
            throw VxphException(SysCode.UserNotExist, "用户名已经存在了")
        }
        user = userRepository.findByEmail(registerReq.email)
        if (user != null) {
            throw VxphException(SysCode.UserNotExist, "邮箱已经注册了")
        }
        if (registerReq.password.length < 6) {
            throw VxphException(SysCode.PasswordTooShort)
        }
        user =
            User(null, registerReq.name, registerReq.email, registerReq.password, registerReq.sex, account, role)
        return userRepository.save(user)
    }


}
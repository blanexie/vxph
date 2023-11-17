package com.github.blanexie.vxph.user.service.impl

import cn.hutool.cache.CacheUtil
import com.github.blanexie.vxph.user.LoginTimeExpireMS
import com.github.blanexie.vxph.user.entity.User
import com.github.blanexie.vxph.user.repository.UserRepository
import com.github.blanexie.vxph.user.service.UserService
import jakarta.annotation.Resource
import org.springframework.stereotype.Service


@Service
class UserServiceImpl(@Resource val userRepository: UserRepository):UserService {

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
}
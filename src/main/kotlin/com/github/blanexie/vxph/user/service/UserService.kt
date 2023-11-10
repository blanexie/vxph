package com.github.blanexie.vxph.user.service

import cn.dev33.satoken.stp.StpUtil
import com.github.blanexie.vxph.user.repository.UserRepository
import jakarta.annotation.Resource
import org.springframework.stereotype.Service

@Service
class UserService(@Resource val userRepository: UserRepository) {

    fun login(name: String, pwdSha256: String, time: Long): Boolean {
        val user = userRepository.findByName(name)
        if (user != null && user.checkPwd(pwdSha256, time)) {
            StpUtil.login(user.id)
            return true
        }
        return false
    }
}
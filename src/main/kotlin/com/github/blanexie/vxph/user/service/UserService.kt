package com.github.blanexie.vxph.user.service

import com.github.blanexie.vxph.user.entity.User
import com.github.blanexie.vxph.user.repository.UserRepository
import jakarta.annotation.Resource
import org.springframework.stereotype.Service

@Service
class UserService(@Resource val userRepository: UserRepository) {

    fun login(name: String, pwdSha256: String, time: Long): User? {
        val user = userRepository.findByName(name)
        if (user != null && user.checkPwd(pwdSha256, time)) {
            return user
        }
        return null
    }
}
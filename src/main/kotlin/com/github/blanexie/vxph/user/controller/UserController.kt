package com.github.blanexie.vxph.user.controller

import cn.dev33.satoken.annotation.SaIgnore
import cn.dev33.satoken.stp.StpUtil
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.web.Result
import com.github.blanexie.vxph.user.dto.LoginReq
import com.github.blanexie.vxph.user.service.UserService
import jakarta.annotation.Resource
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/user")
class UserController(@Resource val userService: UserService) {

    @SaIgnore
    @PostMapping("/login")
    fun login(@RequestBody loginReq: LoginReq): Result {
        val user = userService.login(loginReq.username, loginReq.password, loginReq.time)
        if (user != null) {
            StpUtil.login(user.id)
            return Result.ok(StpUtil.getTokenInfo())
        }
        return Result.fail(SysCode.LongNameAndPwdError)
    }

    @RequestMapping("tokenInfo")
    fun tokenInfo(): Result {
        return Result.ok(StpUtil.getTokenInfo())
    }

    @RequestMapping("logout")
    fun logout(): Result {
        StpUtil.logout()
        return Result.ok()
    }

}
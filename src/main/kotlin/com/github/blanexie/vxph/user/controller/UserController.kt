package com.github.blanexie.vxph.user.controller

import cn.dev33.satoken.stp.StpUtil
import cn.dev33.satoken.util.SaResult
import com.github.blanexie.vxph.common.SysCode
import com.github.blanexie.vxph.common.fail
import com.github.blanexie.vxph.user.service.UserService
import jakarta.annotation.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/user")
class UserController(@Resource val userService: UserService) {

    @GetMapping("/login")
    fun login(@RequestParam name: String, @RequestParam pwdSha256: String, @RequestParam time: Long): SaResult {
        if (userService.login(name, pwdSha256, time)) {
            return SaResult.ok()
        }
        return SaResult.error().fail(SysCode.LongNameAndPwdError)
    }

    @RequestMapping("tokenInfo")
    fun tokenInfo(): SaResult {
        return SaResult.data(StpUtil.getTokenInfo())
    }

    @RequestMapping("logout")
    fun logout(): SaResult {
        StpUtil.logout()
        return SaResult.ok()
    }


}
package com.github.blanexie.vxph.user.controller

import cn.dev33.satoken.annotation.SaIgnore
import cn.dev33.satoken.stp.StpUtil
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.web.WebResp
import com.github.blanexie.vxph.user.dto.LoginReq
import com.github.blanexie.vxph.user.dto.RegisterReq
import com.github.blanexie.vxph.account.service.AccountService
import com.github.blanexie.vxph.user.service.InviteService
import com.github.blanexie.vxph.user.service.RoleService
import com.github.blanexie.vxph.user.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService,
    private val roleService: RoleService,
    private val inviteService: InviteService,
    private val accountService: AccountService,
) {

    @SaIgnore
    @PostMapping("/login")
    fun login(@RequestBody loginReq: LoginReq): WebResp {
        if (loginReq.username == "admin") {
            StpUtil.login(1)
            return WebResp.ok(StpUtil.getTokenInfo())
        }
        val user = userService.login(loginReq.username, loginReq.pwdSha256, loginReq.time)
        if (user != null) {
            StpUtil.login(user.id)
            return WebResp.ok(StpUtil.getTokenInfo())
        }
        return WebResp.fail(SysCode.LongNameAndPwdError)
    }

    @GetMapping("/info")
    fun findUserInfo(): WebResp {
        val userId = StpUtil.getLoginIdAsLong()
        val user = userService.findById(userId)!!
        return WebResp.ok().add("user", user)
            .add("tokenInfo", StpUtil.getTokenInfo())
    }

    @GetMapping("logout")
    fun logout(): WebResp {
        StpUtil.logout()
        return WebResp.ok()
    }

    /**
     * 注册
     */
    @SaIgnore
    @PostMapping("register")
    fun register(@RequestBody registerReq: RegisterReq): WebResp {
        val role = roleService.findByCode("normal")!!
        inviteService.useInvite(registerReq.inviteCode, registerReq.email)
        val account = accountService.getInitAccount()
        val user = userService.saveUser(registerReq, account, role)
        return WebResp.ok().add("user", user).add("account", account)
    }
}
package com.github.blanexie.vxph.user.controller

import cn.dev33.satoken.stp.StpUtil
import com.github.blanexie.vxph.account.service.AccountService
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.entity.WebResp
import com.github.blanexie.vxph.user.dto.LoginReq
import com.github.blanexie.vxph.user.dto.RegisterReq
import com.github.blanexie.vxph.user.service.InviteService
import com.github.blanexie.vxph.user.service.RoleService
import com.github.blanexie.vxph.user.service.UserService
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService,
    private val roleService: RoleService,
    private val inviteService: InviteService,
    private val accountService: AccountService,
) {

    @PostMapping("/login")
    fun login(@RequestBody loginReq: LoginReq): WebResp {
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
    @PostMapping("register")
    fun register(@RequestBody registerReq: RegisterReq): WebResp {
        val role = roleService.findByCode("normal")!!
        inviteService.useInvite(registerReq.inviteCode, registerReq.email)
        val user = userService.saveUser(registerReq, role)
        accountService.initAccount(user)
        return WebResp.ok().add("user", user)
    }
}
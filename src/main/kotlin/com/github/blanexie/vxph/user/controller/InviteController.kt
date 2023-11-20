package com.github.blanexie.vxph.user.controller

import cn.dev33.satoken.stp.StpUtil
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.web.WebResp
import com.github.blanexie.vxph.account.service.AccountService
import com.github.blanexie.vxph.user.service.InviteService
import com.github.blanexie.vxph.user.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/invite")
class InviteController(
    private val inviteService: InviteService,
    private val userService: UserService,
    private val accountService: AccountService,
) {

    @GetMapping("/send")
    fun send(@RequestParam email: String): WebResp {
        val userId = StpUtil.getLoginIdAsLong()
        val user = userService.findById(userId) ?: return WebResp.fail(SysCode.UserNotExist)
        val account = user.account
        if (account.inviteCount < 1) {
            return WebResp.fail(SysCode.UserNotExist)
        }
        val invite = inviteService.saveInviteAndSendMail(email, user)
        account.inviteCount--
        accountService.saveAccount(account)
        return WebResp.ok(invite.code)
    }


}
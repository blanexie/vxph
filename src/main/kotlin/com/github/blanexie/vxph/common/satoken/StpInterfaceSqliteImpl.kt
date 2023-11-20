package com.github.blanexie.vxph.common.satoken

import cn.dev33.satoken.stp.StpInterface
import cn.hutool.core.convert.Convert
import com.github.blanexie.vxph.user.service.RoleService
import com.github.blanexie.vxph.user.service.UserService
import org.springframework.stereotype.Component

@Component
class StpInterfaceSqliteImpl(
    private val userService: UserService,
    private val roleService: RoleService,
) : StpInterface {

    override fun getPermissionList(loginId: Any, loginType: String): List<String> {
        val user = userService.findById(Convert.toLong(loginId))!!
        return user.role.permissions.map { it.code }.toList()
    }

    override fun getRoleList(loginId: Any, loginType: String): List<String> {
        val user = userService.findById(Convert.toLong(loginId))!!
        return listOf(user.role.code)
    }
}
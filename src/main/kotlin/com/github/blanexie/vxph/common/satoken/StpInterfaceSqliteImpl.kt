package com.github.blanexie.vxph.common.satoken

import cn.dev33.satoken.stp.StpInterface
import cn.hutool.core.convert.Convert
import com.github.blanexie.vxph.user.service.PermissionService
import com.github.blanexie.vxph.user.service.RoleService
import com.github.blanexie.vxph.user.service.UserService
import org.springframework.stereotype.Component

@Component
class StpInterfaceSqliteImpl(
    private val userService: UserService,
    private val permissionService: PermissionService
) : StpInterface {

    override fun getPermissionList(loginId: Any, loginType: String): List<String> {
        val userId = Convert.toLong(loginId)
        if (userId == 1L) {
            val permissions = permissionService.findAll()
            return permissions.map { it.code }.toList()
        }
        val user = userService.findById(userId)!!
        return user.role.permissions.map { it.code }.toList()
    }

    override fun getRoleList(loginId: Any, loginType: String): List<String> {
        val user = userService.findById(Convert.toLong(loginId))!!
        return listOf(user.role.code)
    }
}
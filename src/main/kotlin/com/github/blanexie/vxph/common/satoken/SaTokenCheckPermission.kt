package com.github.blanexie.vxph.common.satoken

import cn.dev33.satoken.exception.NotLoginException
import cn.dev33.satoken.`fun`.SaFunction
import cn.dev33.satoken.router.SaRouter
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.collection.CollUtil
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.exception.VxphException
import com.github.blanexie.vxph.user.AnonymouslyRole
import com.github.blanexie.vxph.user.service.RoleService
import com.github.blanexie.vxph.user.service.UserService
import org.springframework.stereotype.Component

@Component
class SaTokenCheckPermission(
    private val userService: UserService,
    private val roleService: RoleService,
) {

    fun checkPermission(permission: String) {
        if (StpUtil.isLogin()) {
            checkLoginUserPermission(permission)
        } else {
            checkAnonymouslyPermission(permission)
        }
    }

    private fun getAuthRules(): Map<String, String> {
        val permissionMap = hashMapOf<String, String>()
        val userId = StpUtil.getLoginIdAsLong()
        val user = userService.findById(userId) ?: return emptyMap()
        user.role.permissions.forEach {
            val path = it.code.split(" ")[1]
            permissionMap[it.code] = path
        }
        return permissionMap
    }

    private fun checkAnonymouslyPermission(permission: String) {
        //获取可以匿名访问的path信息
        val role = roleService.findByCode(AnonymouslyRole)
        val pMap = role?.permissions?.map { it.code }?.toList()
        if (CollUtil.isEmpty(pMap)) {
            throw VxphException(SysCode.PermissionNotAllow)
        }
        if (!pMap!!.contains(permission)) {
            throw VxphException(SysCode.PermissionNotAllow)
        }
    }

    private fun checkLoginUserPermission(permission: String) {
        //获取用户的权限相关path
        val rules = getAuthRules()
        //挨个校验
        if (rules.isEmpty()) {
            throw VxphException(SysCode.PermissionNotAllow)
        }
        for (rule in rules) {
            SaRouter.match(rule.value, SaFunction {
                StpUtil.checkPermission(rule.key)
            })
        }
    }
}
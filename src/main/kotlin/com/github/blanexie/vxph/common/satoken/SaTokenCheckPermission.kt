package com.github.blanexie.vxph.common.satoken

import cn.dev33.satoken.exception.NotLoginException
import cn.dev33.satoken.`fun`.SaFunction
import cn.dev33.satoken.router.SaRouter
import cn.dev33.satoken.stp.StpUtil
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.user.AnonymouslyRole
import com.github.blanexie.vxph.user.service.PermissionService
import com.github.blanexie.vxph.user.service.RoleService
import com.github.blanexie.vxph.user.service.UserService
import org.springframework.stereotype.Component

@Component
class SaTokenCheckPermission(
    private val roleService: RoleService,
    private val permissionService: PermissionService,
) {

    fun checkPermission() {
        if (StpUtil.isLogin()) {
            checkLoginUserPermission()
        } else {
            checkAnonymouslyPermission()
        }
    }

    private fun checkAnonymouslyPermission() {
        //获取可以匿名访问的path信息
        val role = roleService.findByCode(AnonymouslyRole)
        val permissions = role!!.permissions
        var flag = false
        for (permission in permissions) {
            val path = permission.code.split(" ")[1]
            SaRouter.match(path, SaFunction {
                //命中标记
                flag = true
            })
        }
        if (!flag) {
            //都没有命中匿名接口的请求， 直接返回对应异常
            throw NotLoginException(SysCode.NotLoginError.msg, "", "")
        }
    }

    private fun checkLoginUserPermission() {
        val permissions = permissionService.findAll()
        for (permission in permissions) {
            val path = permission.code.split(" ")[1]
            SaRouter.match(path, SaFunction {
                StpUtil.checkPermission(permission.code)
            })
        }
    }
}
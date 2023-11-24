package com.github.blanexie.vxph.user.controller

import cn.dev33.satoken.stp.StpUtil
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.entity.WebResp
import com.github.blanexie.vxph.user.entity.Permission
import com.github.blanexie.vxph.user.entity.Role
import com.github.blanexie.vxph.user.service.PermissionService
import com.github.blanexie.vxph.user.service.RoleService
import com.github.blanexie.vxph.user.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/role")
class RoleController(
    private val roleService: RoleService,
    private val permissionService: PermissionService,
) {
    /**
     * 全量返回所有的角色
     */
    @GetMapping("/list")
    fun list(): WebResp {
        val roles = roleService.findAll()
        return WebResp.ok(roles)
    }

    /**
     *  保存
     */
    @PostMapping("/save")
    fun save(@RequestBody role: Role): WebResp {
        val roles = roleService.saveRole(role)
        return WebResp.ok(roles)
    }

    @GetMapping("/addPermission")
    fun addPermission(roleCode: String, permissionCode: String): WebResp {
        val role = roleService.findByCode(roleCode) ?: return WebResp.fail(SysCode.RoleNotExist)
        val permission = permissionService.findByCode(permissionCode) ?: return WebResp.fail(SysCode.PermissionNotExist)
        val permissions = arrayListOf(permission)
        permissions.addAll(role.permissions)
        role.permissions = permissions
        val saveRole = roleService.saveRole(role)
        return WebResp.ok(saveRole)
    }

    @GetMapping("/removePermission")
    fun removePermission(roleCode: String, permissionCode: String): WebResp {
        val role = roleService.findByCode(roleCode) ?: return WebResp.fail(SysCode.RoleNotExist)
        role.permissions = role.permissions.filter { it.code == permissionCode }.toList()
        val saveRole = roleService.saveRole(role)
        return WebResp.ok(saveRole)
    }
}
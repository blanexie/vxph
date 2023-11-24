package com.github.blanexie.vxph.user.controller

import com.github.blanexie.vxph.common.entity.WebResp
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.user.entity.Role
import com.github.blanexie.vxph.user.service.PermissionService
import com.github.blanexie.vxph.user.service.RoleService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/role")
class RoleController(
    private val roleService: RoleService,
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

    @GetMapping("/delete")
    fun delete(roleCode: String): WebResp {
       roleService.delete(roleCode)
        return WebResp.ok()
    }

}
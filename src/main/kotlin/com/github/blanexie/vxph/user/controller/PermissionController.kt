package com.github.blanexie.vxph.user.controller

import com.github.blanexie.vxph.common.entity.PageReq
import com.github.blanexie.vxph.common.entity.WebResp
import com.github.blanexie.vxph.user.service.PermissionService
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/permission")
class PermissionController(
    private val permissionService: PermissionService
) {

    /**
     * 全量返回所有的角色
     */
    @PostMapping("/query")
    fun list(@RequestBody pageReq: PageReq): WebResp {
        val permissionPage = permissionService.find(
            pageReq.searchKey,
            PageRequest.of(pageReq.page - 1, pageReq.pageSize)
        )
        return WebResp.ok(permissionPage)
    }

    /**
     * 全量返回所有的角色
     */
    @GetMapping("/findAll")
    fun findAll(): WebResp {
        val permissionPage = permissionService.findAll()
        return WebResp.ok(permissionPage)
    }



}
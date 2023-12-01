package com.github.blanexie.vxph.user.service

import com.github.blanexie.vxph.user.entity.Permission
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.Query

interface PermissionService {

    fun findByCode(code: String): Permission?

    fun findAll():List<Permission>

    fun find(searchKey:String?, pageRequest: PageRequest): Page<Permission>

}
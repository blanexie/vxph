package com.github.blanexie.vxph.user.repository

import com.github.blanexie.vxph.user.dto.PermissionType
import com.github.blanexie.vxph.user.entity.Permission
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor

interface PermissionRepository : CrudRepository<Permission, Long>, QueryByExampleExecutor<Permission> {

    fun findByCode(code: String): Permission?
    fun findByCodeAndType(code: String, permissionType: PermissionType): Permission?

}
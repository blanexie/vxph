package com.github.blanexie.vxph.user.repository

import cn.hutool.core.util.StrUtil
import com.github.blanexie.vxph.common.getBean
import com.github.blanexie.vxph.user.dto.PermissionType
import com.github.blanexie.vxph.user.entity.Permission
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor

interface PermissionRepository : CrudRepository<Permission, Long>, QueryByExampleExecutor<Permission> {
    fun findByCodeIn(code: List<String>): List<Permission>

    fun findByCode(code: String): Permission?
    fun findByCodeAndType(code: String, permissionType: PermissionType): Permission?


}
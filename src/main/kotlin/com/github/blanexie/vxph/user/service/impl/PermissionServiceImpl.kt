package com.github.blanexie.vxph.user.service.impl

import com.github.blanexie.vxph.user.entity.Permission
import com.github.blanexie.vxph.user.repository.PermissionRepository
import com.github.blanexie.vxph.user.service.PermissionService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class PermissionServiceImpl(
    private val permissionRepository: PermissionRepository,
) : PermissionService {


    override fun findByCode(code: String): Permission? {
        return permissionRepository.findByCode(code)
    }

    override fun find(pageRequest: PageRequest): Page<Permission> {
        return permissionRepository.find(pageRequest)
    }

}
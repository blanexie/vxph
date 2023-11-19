package com.github.blanexie.vxph.user.service.impl

import com.github.blanexie.vxph.user.entity.Permission
import com.github.blanexie.vxph.user.repository.PermissionRepository
import com.github.blanexie.vxph.user.service.PermissionService
import org.springframework.stereotype.Service

@Service
class PermissionServiceImpl(
    private val permissionRepository: PermissionRepository,
) : PermissionService {


    override fun findById(id: Long): Permission {
        return permissionRepository.findById(id).get()
    }


}
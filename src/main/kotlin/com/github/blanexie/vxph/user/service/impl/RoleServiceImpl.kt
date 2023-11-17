package com.github.blanexie.vxph.user.service.impl

import com.github.blanexie.vxph.user.entity.Role
import com.github.blanexie.vxph.user.repository.RoleRepository
import com.github.blanexie.vxph.user.service.RoleService
import org.springframework.stereotype.Service

@Service
class RoleServiceImpl(
    private val roleRepository: RoleRepository
) : RoleService {

    override fun findByCode(code: String): Role? {
        return roleRepository.findByCode(code)
    }

}
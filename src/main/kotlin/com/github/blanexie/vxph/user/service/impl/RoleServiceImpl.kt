package com.github.blanexie.vxph.user.service.impl

import com.github.blanexie.vxph.user.entity.Role
import com.github.blanexie.vxph.user.repository.RoleRepository
import com.github.blanexie.vxph.user.service.RoleService
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RoleServiceImpl(
    private val roleRepository: RoleRepository,
    private val entityManager: EntityManager,
) : RoleService {

    override fun findByCode(code: String): Role? {
        return roleRepository.findByCode(code)
    }

    override fun findAll(): List<Role> {
        return roleRepository.findAll().toList()
    }

    override fun saveRole(role: Role): Role {
        return roleRepository.save(role)
    }

    @Transactional
    override fun delete(code: String) {
        val role = roleRepository.findByCode(code)
        role?.let { entityManager.remove(it) }
    }

}
package com.github.blanexie.vxph.user.repository

import com.github.blanexie.vxph.user.entity.Role
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor

interface RoleRepository : CrudRepository<Role, Long>, QueryByExampleExecutor<Role> {

    fun findByCode(code: String): Role?



}
package com.github.blanexie.vxph.user.repository

import com.github.blanexie.vxph.user.entity.User
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor

interface UserRepository : CrudRepository<User, Long>, QueryByExampleExecutor<User> {

    fun findByName(name: String): User?

}
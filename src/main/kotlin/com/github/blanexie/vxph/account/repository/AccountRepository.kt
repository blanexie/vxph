package com.github.blanexie.vxph.account.repository


import com.github.blanexie.vxph.account.entity.Account
import com.github.blanexie.vxph.user.entity.User
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor


interface AccountRepository : CrudRepository<Account, Long>, QueryByExampleExecutor<Account> {

    fun findByUser(user: User): Account

}
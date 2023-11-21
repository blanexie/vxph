package com.github.blanexie.vxph.account.service

import com.github.blanexie.vxph.account.entity.Account
import com.github.blanexie.vxph.user.entity.User

interface AccountService {

    fun saveAccount(account: Account): Account

    fun initAccount(user: User): Account

    fun findByUser(user: User): Account

}
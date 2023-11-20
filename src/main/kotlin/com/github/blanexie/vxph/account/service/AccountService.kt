package com.github.blanexie.vxph.account.service

import com.github.blanexie.vxph.account.entity.Account

interface AccountService {

    fun saveAccount(account: Account): Account

    fun getInitAccount(): Account

}
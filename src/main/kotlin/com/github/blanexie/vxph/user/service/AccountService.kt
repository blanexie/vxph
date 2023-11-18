package com.github.blanexie.vxph.user.service

import com.github.blanexie.vxph.user.entity.Account

interface AccountService {

    fun saveAccount(account: Account): Account


    fun getInitAccount():Account

}
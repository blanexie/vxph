package com.github.blanexie.vxph.account.service.impl

import com.github.blanexie.vxph.account.entity.Account
import com.github.blanexie.vxph.account.repository.AccountRepository
import com.github.blanexie.vxph.account.service.AccountService
import com.github.blanexie.vxph.user.entity.User
import org.springframework.stereotype.Service

@Service
class AccountServiceImpl(
    private val accountRepository: AccountRepository,
) : AccountService {

    override fun saveAccount(account: Account): Account {
        return accountRepository.save(account)
    }

    override fun initAccount(user: User): Account {
        val account = Account(
            null, 0, 0, 0, 0, 0, "1", 0, user
        )
        return accountRepository.save(account)
    }

    override fun findByUser(user: User): Account {
        return accountRepository.findByUser(user)
    }

}
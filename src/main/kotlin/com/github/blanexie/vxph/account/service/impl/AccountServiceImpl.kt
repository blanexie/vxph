package com.github.blanexie.vxph.account.service.impl

import com.github.blanexie.vxph.account.entity.Account
import com.github.blanexie.vxph.account.repository.AccountRepository
import com.github.blanexie.vxph.account.service.AccountService
import org.springframework.stereotype.Service

@Service
class AccountServiceImpl(
    private val accountRepository: AccountRepository,
) : AccountService {


    override fun saveAccount(account: Account): Account {
        return accountRepository.save(account)
    }

    override fun getInitAccount(): Account {
        return Account(
            null, 0, 0, 0, 0, 0, "1", 0
        )
    }
}
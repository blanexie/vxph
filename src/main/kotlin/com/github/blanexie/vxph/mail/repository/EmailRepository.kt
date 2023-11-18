package com.github.blanexie.vxph.mail.repository

import com.github.blanexie.vxph.mail.entity.Email
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor

interface EmailRepository : CrudRepository<Email, Long>, QueryByExampleExecutor<Email> {}
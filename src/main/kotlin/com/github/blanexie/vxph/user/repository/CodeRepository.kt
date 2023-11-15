package com.github.blanexie.vxph.user.repository

import com.github.blanexie.vxph.user.entity.Code
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor

interface CodeRepository : CrudRepository<Code, Long>, QueryByExampleExecutor<Code> {

    fun findAllByCode(code: String): Code?

}
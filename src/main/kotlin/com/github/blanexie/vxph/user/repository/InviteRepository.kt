package com.github.blanexie.vxph.user.repository

import com.github.blanexie.vxph.user.entity.Invite
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor

interface InviteRepository : CrudRepository<Invite, Long>, QueryByExampleExecutor<Invite> {
}
package com.github.blanexie.vxph.user.repository

import com.github.blanexie.vxph.user.entity.Path
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor

interface PathRepository : CrudRepository<Path, Long>, QueryByExampleExecutor<Path> {
}
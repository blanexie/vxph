package com.github.blanexie.vxph.torrent.repository

import com.github.blanexie.vxph.torrent.entity.Label
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor

interface LabelRepository : CrudRepository<Label, Long>, QueryByExampleExecutor<Label> {

    fun findByCodeIn(codes: List<String>): List<Label>

}
package com.github.blanexie.vxph.torrent.repository

import com.github.blanexie.vxph.torrent.entity.FileResource
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor

interface FileResourceRepository : CrudRepository<FileResource, Long>, QueryByExampleExecutor<FileResource> {

    fun deleteByHash(hash: String)

    fun findAllByHashIn(hashs: List<String>): List<FileResource>

    fun findByHash(hash: String): FileResource
}
package com.github.blanexie.vxph.torrent.service

import com.github.blanexie.vxph.torrent.entity.FileResource


interface FileResourceService {

    fun findAllByHashIn(hashs: List<String>): List<FileResource>

    fun deleteByHash(hash: String): FileResource?

    fun findByHash(hash: String): FileResource?

    fun saveFile(fileResource: FileResource): FileResource

}
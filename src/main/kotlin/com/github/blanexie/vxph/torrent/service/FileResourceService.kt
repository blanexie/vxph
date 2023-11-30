package com.github.blanexie.vxph.torrent.service

import com.github.blanexie.vxph.torrent.entity.FileResource
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile


interface FileResourceService {

    fun findAllByHashIn(hashs: List<String>): List<FileResource>

    fun deleteByHash(hash: String): FileResource?

    fun findByHash(hash: String): FileResource?

    fun saveFile(fileResource: FileResource): FileResource

    fun saveFile(file: MultipartFile,  hash: String,loginUserId:Long): FileResource
}
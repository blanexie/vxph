package com.github.blanexie.vxph.torrent.service

import com.github.blanexie.vxph.torrent.entity.FileResource
import com.github.blanexie.vxph.torrent.repository.FileResourceRepository
import org.springframework.stereotype.Service

@Service
class FileResourceService(
    private val fileResourceRepository: FileResourceRepository
) {

    fun findAllByHashIn(hashs: List<String>):List<FileResource> {
        return fileResourceRepository.findAllByHashIn(hashs)
    }


}
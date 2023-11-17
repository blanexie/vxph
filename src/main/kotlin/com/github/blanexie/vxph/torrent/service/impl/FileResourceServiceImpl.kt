package com.github.blanexie.vxph.torrent.service.impl

import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.exception.VxphException
import com.github.blanexie.vxph.common.objectMapper
import com.github.blanexie.vxph.torrent.util.File_Allow_Suffix_Code
import com.github.blanexie.vxph.torrent.entity.FileResource
import com.github.blanexie.vxph.torrent.repository.FileResourceRepository
import com.github.blanexie.vxph.torrent.service.FileResourceService
import com.github.blanexie.vxph.user.service.CodeService
import org.springframework.stereotype.Service
import java.io.File

@Service
class FileResourceServiceImpl(
    private val fileResourceRepository: FileResourceRepository,
    private val codeService: CodeService,
) : FileResourceService {

    override fun findAllByHashIn(hashs: List<String>): List<FileResource> {
        return fileResourceRepository.findAllByHashIn(hashs)
    }

    override fun deleteByHash(hash: String): FileResource {
        val fileResource = fileResourceRepository.findByHash(hash)
        fileResourceRepository.deleteByHash(hash)
        return fileResource
    }

    override fun findByHash(hash: String): FileResource {
        return fileResourceRepository.findByHash(hash)
    }

    override fun saveFile(fileResource: FileResource): FileResource {
        val codeValue = codeService.findValueByCode(File_Allow_Suffix_Code)
        val suffixs = objectMapper.readValue(codeValue, List::class.java)
        if (!suffixs.contains(fileResource.suffix)) {
            throw VxphException(SysCode.NotAllowFile)
        }
        return fileResourceRepository.save(fileResource)
    }

}
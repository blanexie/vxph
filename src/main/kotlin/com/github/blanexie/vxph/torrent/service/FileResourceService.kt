package com.github.blanexie.vxph.torrent.service

import cn.hutool.cache.CacheUtil
import cn.hutool.core.io.FileUtil
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.exception.VxphException
import com.github.blanexie.vxph.common.objectMapper
import com.github.blanexie.vxph.torrent.File_Allow_Suffix_Code
import com.github.blanexie.vxph.torrent.dto.FileResourceDTO
import com.github.blanexie.vxph.torrent.entity.FileResource
import com.github.blanexie.vxph.torrent.repository.FileResourceRepository
import com.github.blanexie.vxph.user.entity.User
import com.github.blanexie.vxph.user.service.CodeService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File

@Service
class FileResourceService(
    private val fileResourceRepository: FileResourceRepository,
    private val codeService: CodeService,
    @Value("\${vxph.file.path}")
    private val filePath: String,
) {

    fun findAllByHashIn(hashs: List<String>): List<FileResource> {
        return fileResourceRepository.findAllByHashIn(hashs)
    }

    fun deleteByHash(hash: String) {
        return fileResourceRepository.deleteByHash(hash)
    }

    fun findByHash(hash: String): FileResource {
        return fileResourceRepository.findByHash(hash)
    }

    fun findFile(hash: String): FileResourceDTO {
        val fileResource = fileResourceRepository.findByHash(hash)
        return FileResourceDTO(File("${filePath}/${fileResource.hash}.${fileResource.suffix}"), fileResource)
    }

    fun saveFile(fileResource: FileResource, file: File): FileResource {
        val codeValue = codeService.findValueByCode(File_Allow_Suffix_Code)
        val suffixs = objectMapper.readValue(codeValue, List::class.java)
        if (!suffixs.contains(fileResource.suffix)) {
            throw VxphException(SysCode.NotAllowFile)
        }
        FileUtil.move(file, File("${filePath}/${fileResource.hash}.${fileResource.suffix}"), true)
        return fileResourceRepository.save(fileResource)
    }

}
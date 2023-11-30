package com.github.blanexie.vxph.torrent.service.impl

import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.IoUtil
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.exception.VxphException
import com.github.blanexie.vxph.common.objectMapper
import com.github.blanexie.vxph.torrent.util.File_Allow_Suffix_Code
import com.github.blanexie.vxph.torrent.entity.FileResource
import com.github.blanexie.vxph.torrent.repository.FileResourceRepository
import com.github.blanexie.vxph.torrent.service.FileResourceService
import com.github.blanexie.vxph.user.service.CodeService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Service
class FileResourceServiceImpl(
    private val fileResourceRepository: FileResourceRepository,
    private val codeService: CodeService,
    @Value("\${vxph.data.dir}")
    private val filePath: String,
) : FileResourceService {

    override fun findAllByHashIn(hashs: List<String>): List<FileResource> {
        return fileResourceRepository.findAllByHashIn(hashs)
    }

    override fun deleteByHash(hash: String): FileResource? {
        val fileResource = fileResourceRepository.findByHash(hash)
        fileResource?.let {
            fileResourceRepository.deleteByHash(hash)
        }
        return fileResource
    }

    override fun findByHash(hash: String): FileResource? {
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

    override fun saveFile(file: MultipartFile, hash: String, loginUserId: Long): FileResource {
        //获取后缀
        val suffix = FileUtil.getSuffix(file.originalFilename)
        val fileResource = FileResource(hash, file.originalFilename, suffix, file.size, loginUserId)
        //先保存文件，再保存数据库记录，保证数据库中的记录必定会有对应的文件
        val saveFile = File("${filePath}/file/${fileResource.hash}.${fileResource.suffix}")
        saveFile.outputStream().use {
            IoUtil.copy(file.inputStream, it)
        }
        return this.saveFile(fileResource)
    }

}
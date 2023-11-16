package com.github.blanexie.vxph.user.service.impl

import cn.hutool.core.convert.Convert
import cn.hutool.core.util.StrUtil
import com.github.blanexie.vxph.common.objectMapper
import com.github.blanexie.vxph.torrent.Announce_Url_Code
import com.github.blanexie.vxph.user.repository.CodeRepository
import com.github.blanexie.vxph.user.service.CodeService
import org.springframework.stereotype.Service

@Service
class CodeServiceImpl(val codeRepository: CodeRepository) : CodeService {


    override fun findAnnounceUrl(): List<String> {
        val code = codeRepository.findFirstByCode(Announce_Url_Code) ?: return emptyList()
        val announceUrls = objectMapper.readValue(code.value, List::class.java)
        return announceUrls.map { Convert.toStr(it) }.filter { StrUtil.isNotBlank(it) }.toList()
    }

    override fun findValueByCode(code: String): String? {
        val code = codeRepository.findFirstByCode(code)
        return code?.value
    }

}
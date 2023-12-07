package com.github.blanexie.vxph.user.controller

import com.github.blanexie.vxph.common.entity.WebResp
import com.github.blanexie.vxph.user.service.CodeService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/code")
class CodeController(val codeService: CodeService) {

    @GetMapping("type")
    fun findByType(code: String): WebResp {
        val code = codeService.findValueByCode(code)
        return WebResp.ok().add("type", code ?: "")
    }

}
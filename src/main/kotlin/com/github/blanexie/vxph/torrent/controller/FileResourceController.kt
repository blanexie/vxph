package com.github.blanexie.vxph.torrent.controller

import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.io.IoUtil
import com.github.blanexie.vxph.common.entity.WebResp
import com.github.blanexie.vxph.torrent.service.FileResourceService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedOutputStream
import java.io.File

@Controller
@RequestMapping("/api/resource")
class FileResourceController(
    private val fileResourceService: FileResourceService,
    @Value("\${vxph.data.dir}")
    private val filePath: String,
) {


    @ResponseBody
    @PostMapping("/upload")
    fun uploadFile(@RequestPart file: MultipartFile, @RequestParam hash: String): WebResp {
        val loginUserId = StpUtil.getLoginIdAsLong()
        //检查是否已经存在
        val findByHash = fileResourceService.findByHash(hash)
        if (findByHash != null) {
            return WebResp.ok(findByHash)
        }
        val resource = fileResourceService.saveFile(file, hash, loginUserId)
        return WebResp.ok(resource)
    }

    @GetMapping("/{hash}.{suffix}")
    fun download(@PathVariable("hash") hash: String, @PathVariable("suffix") suffix: String, response: HttpServletResponse) {
        val fileResource = fileResourceService.findByHash(hash)
        if (fileResource != null) {
            response.setHeader("Content-Type", "image/${fileResource.suffix}")
            response.setHeader("Cache-Control", "max-age=604800, public ")
            response.setHeader("Pragma", "public")
            val bufferedInputStream = BufferedOutputStream(response.outputStream)
            val file = File("$filePath/file/${fileResource.hash}.${fileResource.suffix}")
            file.inputStream().use {
                IoUtil.copy(it, bufferedInputStream)
            }
        } else {
            response.status = 404
        }
        response.flushBuffer()
    }

    @GetMapping("/delete")
    fun delete(@RequestParam("hash") hash: String): WebResp {
        val fileResource = fileResourceService.deleteByHash(hash)
        fileResource?.let {
            File("$filePath/file/${it.hash}.${it.suffix}").deleteOnExit()
        }
        return WebResp.ok()
    }

}
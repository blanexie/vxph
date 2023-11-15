package com.github.blanexie.vxph.torrent.controller

import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.convert.Convert
import cn.hutool.crypto.digest.DigestUtil
import com.dampcake.bencode.Type
import com.github.blanexie.vxph.common.bencode
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.objectMapper
import com.github.blanexie.vxph.common.web.Result
import com.github.blanexie.vxph.torrent.entity.Torrent
import com.github.blanexie.vxph.torrent.service.PostService
import com.github.blanexie.vxph.torrent.service.TorrentService
import com.github.blanexie.vxph.user.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File

@RequestMapping("/api/torrent")
@RestController
class TorrentController(
    val torrentService: TorrentService,
    val postService: PostService,
    val userService: UserService,
    @Value("\${vxph.torrent.path}")
    val torrentPath: String,
) {

    @PostMapping("/upload")
    fun torrentUpload(
        @RequestPart file: MultipartFile,
        @RequestParam postId: Long,
        @RequestParam title: String
    ): Result {
        //1. 判断postId是否存在
        val post = postService.findByPostId(postId) ?: return Result.fail(SysCode.PostNotExist)
        //2. 解析文件内容
        val torrentMap = bencode.decode(file.bytes, Type.DICTIONARY)
        val info = getPrivateInfo(torrentMap)
        val pieceLength = Convert.toLong(info["piece length"])
        val infoBytes = bencode.encode(info)
        val infoHash = DigestUtil.sha1Hex(infoBytes)
        val length = getLength(info)
        val name = Convert.toStr(info["name"])
        val comment = Convert.toStr(torrentMap["comment"], "")
        val fileStr = getFileStr(info)
        val createDate = Convert.toLong(torrentMap["creation date"], 0)
        val createBy = Convert.toStr(torrentMap["created by"], "")
        val single = info.containsKey("files")
        val user = userService.findById(StpUtil.getLoginIdAsLong())

        val torrent = Torrent(
            infoHash, title, name, length, comment, fileStr, createDate, createBy, pieceLength, single, 0,
            0, 0, arrayListOf(), user!!, post
        )
        //3. 保存文件和修改数据库
        File("${torrentPath}/${infoHash}").writeBytes(infoBytes)
        torrentService.save(torrent)
        return Result.ok()
    }

    @PostMapping("/download")
    fun torrentDownload(
        @RequestParam infoHash: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val byteBuffer = torrentService.buildTorrentBytes(infoHash)
        response.outputStream.write(byteBuffer.array())
        response.flushBuffer()
    }

    @PostMapping("/query")
    fun torrentQuery() {

    }

    private fun getPrivateInfo(torrentMap: Map<String, Any>): HashMap<String, Any> {
        val info = torrentMap["info"] as HashMap<String, Any>
        info["private"] = 1
        return info
    }

    private fun getFileStr(info: HashMap<String, Any>): String {
        val result = arrayListOf<Map<String, Any>>()
        if (info.containsKey("files")) {
            val files = info["files"] as List<Map<String, Any>>
            result.addAll(files)
        } else {
            info.remove("pieces")
            result.add(info)
        }
        return objectMapper.writeValueAsString(result)
    }

    private fun getLength(info: MutableMap<String, Any>): Long {
        return if (info.containsKey("files")) {
            //多文件
            val files = info["files"] as List<Map<String, Any>>
            files.sumOf { Convert.toLong(it["length"]) }
        } else {
            //单文件
            Convert.toLong(info["length"])
        }

    }


}
package com.github.blanexie.vxph.torrent.controller

import cn.dev33.satoken.stp.StpUtil
import com.dampcake.bencode.Type
import com.github.blanexie.vxph.common.bencode
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.exception.VxphException
import com.github.blanexie.vxph.common.entity.WebResp
import com.github.blanexie.vxph.torrent.service.PeerService
import com.github.blanexie.vxph.torrent.service.PostService
import com.github.blanexie.vxph.torrent.service.TorrentService
import com.github.blanexie.vxph.user.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RequestMapping("/api/torrent")
@RestController
class TorrentController(
    val torrentService: TorrentService,
    val postService: PostService,
    val userService: UserService,
    val peerService: PeerService,
) {

    @PostMapping("/upload")
    fun torrentUpload(@RequestPart file: MultipartFile, @RequestParam postId: Long, @RequestParam title: String): WebResp {
        val user = userService.findById(StpUtil.getLoginIdAsLong())!!
        val post = postService.findByPostId(postId) ?: return WebResp.fail(SysCode.PostNotExist)
        val torrentMap = bencode.decode(file.bytes, Type.DICTIONARY)
        val torrent = torrentService.saveTorrent(torrentMap, user, post, title)
        return WebResp.ok(torrent.infoHash)
    }

    @GetMapping("/download")
    fun torrentDownload(@RequestParam infoHash: String, request: HttpServletRequest, response: HttpServletResponse) {
        val user = userService.findById(StpUtil.getLoginIdAsLong())!!
        val torrent = torrentService.findByInfoHash(infoHash) ?: throw VxphException(SysCode.TorrentNotExist)
        val peer = peerService.checkAndSave(user, torrent)
        torrentService.writeTorrentBytes(peer, torrent, response.outputStream)
        response.flushBuffer()
    }

    @PostMapping("/query")
    fun torrentQuery() {

    }


}
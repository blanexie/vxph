package com.github.blanexie.vxph.torrent.service

import com.github.blanexie.vxph.torrent.entity.Label

interface LabelService {

    fun findAll(): List<Label>

}
package com.github.blanexie.vxph.torrent.service.impl

import com.github.blanexie.vxph.torrent.entity.Label
import com.github.blanexie.vxph.torrent.repository.LabelRepository
import com.github.blanexie.vxph.torrent.service.LabelService
import org.springframework.stereotype.Service

@Service
class LabelServiceImpl(
    private val labelRepository: LabelRepository
) : LabelService {

    override fun findAll(): List<Label> {
        return labelRepository.findAll().toList()
    }

}
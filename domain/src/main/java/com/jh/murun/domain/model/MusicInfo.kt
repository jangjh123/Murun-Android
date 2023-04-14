package com.jh.murun.domain.model

import com.jh.murun.domain.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class MusicInfo(
    val uuid: String,
    val title: String,
    val artist: String,
    val albumImage: String?,
    val bpm: Int,
    val url: String,
    var diskPath: String?
) : BaseModel

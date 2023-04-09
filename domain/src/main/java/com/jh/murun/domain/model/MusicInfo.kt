package com.jh.murun.domain.model

import com.jh.murun.domain.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class MusicInfo(
    val uuid: String,
    val title: String,
    val artist: String,
    val musicUrl: String,
    var musicPath: String?,
    val albumImage: String?,
    val bpm: Int
) : BaseModel

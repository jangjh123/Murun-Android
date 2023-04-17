package com.jh.murun.domain.model

import com.jh.murun.domain.base.BaseModel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Music(
    val id: String,
    val title: String,
    val artist: String,
    val duration: Long,
    val bpm: Int? = null,
    val imageUrl: String? = null,
    var image: @RawValue ByteArray? = null,
    val fileUrl: String? = null,
    var diskPath: String? = null
) : BaseModel

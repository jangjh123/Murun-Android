package com.jh.murun.domain.model

import com.jh.murun.domain.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Music(
    val uuid: String,
    val title: String,
    val artist: String,
    val musicPath: String,
    val albumImage: String?,
    val bpm: Int
): BaseModel

package com.jh.murun.domain.model

import com.jh.murun.domain.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MusicList(
    val musics: List<Music>,
    val musicCount: Int
) : BaseModel

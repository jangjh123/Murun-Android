package com.jh.murun.domain.model

import com.jh.murun.domain.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class MusicList(
    val musicList: List<Music>
) : BaseModel

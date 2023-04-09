package com.jh.murun.domain.model

import com.jh.murun.domain.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class MusicList(
    val musicInfoList: List<MusicInfo>
) : BaseModel

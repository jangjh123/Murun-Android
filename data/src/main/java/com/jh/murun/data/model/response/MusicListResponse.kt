package com.jh.murun.data.model.response

import com.google.gson.annotations.SerializedName
import com.jh.murun.data.base.BaseResponse
import com.jh.murun.domain.model.MusicList
import kotlinx.parcelize.Parcelize

@Parcelize
data class MusicListResponse(
    @SerializedName("list")
    val list: List<MusicResponse>
) : BaseResponse {
    override fun toDataModel(): MusicList {
        return MusicList(
            musicList = list.map { it.toDataModel() }
        )
    }
}
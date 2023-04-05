package com.jh.murun.data.model.response

import com.jh.murun.data.base.BaseResponse
import com.jh.murun.data.mapper.DataMapper
import com.jh.murun.data.model.response.MusicResponse.Companion.toDataModel
import com.jh.murun.domain.model.MusicList
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MusicListResponse(
    val musics: List<MusicResponse>?,
    val musicCount: Int?
) : BaseResponse {
    companion object : DataMapper<MusicListResponse, MusicList> {
        override fun MusicListResponse.toDataModel(): MusicList {
            return MusicList(
                musics = musics?.map { it.toDataModel() } ?: emptyList(),
                musicCount = musicCount ?: 0
            )
        }
    }
}

package com.jh.murun.data.model.response

import com.jh.murun.data.base.BaseResponse
import com.jh.murun.data.mapper.DataMapper
import com.jh.murun.domain.model.Music
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MusicResponse(
    val musicUrl: String?,
    val bpm: Int?,
    val title: String?,
    val artist: String?,
    val albumImage: String?
) : BaseResponse {
    companion object : DataMapper<MusicResponse, Music> {
        override fun MusicResponse.toDataModel(): Music {
            return Music(
                title = title ?: "No Title",
                artist = artist ?: "No Artist",
                bpm = bpm ?: 0,
                musicPath = musicUrl ?: "", // TODO : 디스크에 쓰기 기능 구현 후 수정
                albumImage = albumImage ?: ""
            )
        }
    }
}

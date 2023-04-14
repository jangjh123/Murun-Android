package com.jh.murun.data.model.response

import com.jh.murun.data.base.BaseResponse
import com.jh.murun.data.mapper.DataMapper
import com.jh.murun.domain.model.MusicInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class MusicInfoResponse(
    val uuid: String?,
    val url: String?,
    val bpm: Int?,
    val title: String?,
    val artist: String?,
    val albumImage: String?
) : BaseResponse {
    companion object : DataMapper<MusicInfoResponse, MusicInfo> {
        override fun MusicInfoResponse.toDataModel(): MusicInfo {
            return MusicInfo(
                uuid = uuid ?: "",
                title = title ?: "No Title",
                artist = artist ?: "No Artist",
                bpm = bpm ?: 0,
                url = url ?: "",
                diskPath = null,
                albumImage = albumImage ?: ""
            )
        }
    }
}

package com.jh.murun.data.model.response

import com.google.gson.annotations.SerializedName
import com.jh.murun.data.base.BaseResponse
import com.jh.murun.domain.model.Music
import kotlinx.parcelize.Parcelize

@Parcelize
data class MusicResponse(
    @SerializedName("uuid")
    val uuid: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("artist")
    val artist: String?,
    @SerializedName("time")
    val time: Long?,
    @SerializedName("albumImage")
    val albumImage: String?,
    @SerializedName("url")
    val url: String?
) : BaseResponse {
    override fun toDataModel(): Music {
        return Music(
            id = uuid ?: "",
            title = title ?: "No Title",
            artist = artist ?: "No Artist",
            duration = time ?: 0L,
            isStored = false,
            imageUrl = albumImage,
            fileUrl = url
        )
    }
}

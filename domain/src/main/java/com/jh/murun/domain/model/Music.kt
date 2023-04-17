package com.jh.murun.domain.model

import com.jh.murun.domain.base.BaseModel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Music(
    val id: String,
    val title: String,
    val artist: String,
    val bpm: Int? = null,
    val imageUrl: String? = null,
    var image: @RawValue ByteArray? = null,
    val fileUrl: String? = null,
    var diskPath: String? = null
) : BaseModel {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Music

        if (id != other.id) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (bpm != other.bpm) return false
        if (imageUrl != other.imageUrl) return false
        if (image != null) {
            if (other.image == null) return false
            if (!image.contentEquals(other.image)) return false
        } else if (other.image != null) return false
        if (fileUrl != other.fileUrl) return false
        if (diskPath != other.diskPath) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + (bpm ?: 0)
        result = 31 * result + (imageUrl?.hashCode() ?: 0)
        result = 31 * result + (image?.contentHashCode() ?: 0)
        result = 31 * result + (fileUrl?.hashCode() ?: 0)
        result = 31 * result + (diskPath?.hashCode() ?: 0)
        return result
    }
}

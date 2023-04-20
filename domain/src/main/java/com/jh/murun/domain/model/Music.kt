package com.jh.murun.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jh.murun.domain.base.BaseModel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@Entity
data class Music(
    @PrimaryKey
    val id: String,
    val title: String,
    val artist: String,
    val duration: Long,
    val bpm: Int? = null,
    val imageUrl: String? = null,
    var image: ByteArray? = null,
    val fileUrl: String? = null,
    var diskPath: String? = null
) : BaseModel

package com.jh.murun.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jh.murun.domain.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Music(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val artist: String,
    val duration: Long,
    val imageUrl: String? = null,
    var image: ByteArray? = null,
    val url: String?
) : BaseModel

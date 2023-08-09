package com.jh.murun.domain.model

import com.jh.murun.domain.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Error (
    val code: Int?,
    val message: String?
) : BaseModel

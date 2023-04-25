package com.jh.murun.data.base

import android.os.Parcelable
import com.jh.murun.domain.base.BaseModel

interface BaseResponse : Parcelable {
    fun toDataModel(): BaseModel
}
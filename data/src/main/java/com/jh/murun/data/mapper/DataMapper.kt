package com.jh.murun.data.mapper

import com.jh.murun.data.base.BaseResponse
import com.jh.murun.domain.base.BaseModel

interface DataMapper<in R : BaseResponse, out D : BaseModel> {
    fun R.toDataModel(): D
}
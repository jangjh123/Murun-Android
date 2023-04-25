package com.jh.murun.data.model.response

import com.jh.murun.data.base.BaseResponse
import com.jh.murun.domain.model.Error
import kotlinx.parcelize.Parcelize

@Parcelize
data class ErrorResponse(
    val code: Int? = null,
    val message: String? = null
) : BaseResponse {
    override fun toDataModel(): Error {
        return Error(
            code = code ?: 0,
            message = message ?: "Unknown Error"
        )
    }
}
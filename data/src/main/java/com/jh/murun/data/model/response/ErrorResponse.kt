package com.jh.murun.data.model.response

import com.jh.murun.data.base.BaseResponse
import com.jh.murun.data.mapper.DataMapper
import com.jh.murun.domain.model.NetworkError
import kotlinx.parcelize.Parcelize

@Parcelize
data class ErrorResponse(
    val code: Int? = null,
    val message: String? = null
) : BaseResponse {
    companion object : DataMapper<ErrorResponse, NetworkError> {
        override fun ErrorResponse.toDataModel(): NetworkError {
            return NetworkError(
                code = code ?: 0,
                message = message ?: "Unknown Error"
            )
        }
    }
}
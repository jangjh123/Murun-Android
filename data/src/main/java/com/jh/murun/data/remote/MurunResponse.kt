package com.jh.murun.data.remote

import com.jh.murun.data.model.response.ErrorResponse

sealed class MurunResponse<out T> {
    data class Success<T>(val data: T) : MurunResponse<T>()
    data class Error(val error: ErrorResponse) : MurunResponse<Nothing>()
}

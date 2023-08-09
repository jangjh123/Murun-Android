package com.jh.murun.data.remote

import com.jh.murun.data.model.response.ErrorResponse

sealed class MurunResponse<out T> {
    data class OnSuccess<T>(val data: T) : MurunResponse<T>()
    data class OnError(val error: ErrorResponse) : MurunResponse<Nothing>()
}

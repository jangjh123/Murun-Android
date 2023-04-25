package com.jh.murun.domain.model

sealed class ResponseState<out T> {
    data class OnSuccess<T>(val data: T) : ResponseState<T>()
    data class OnError(val error: Error?) : ResponseState<Nothing>()
}
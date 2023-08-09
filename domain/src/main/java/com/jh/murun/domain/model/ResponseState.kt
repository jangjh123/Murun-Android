package com.jh.murun.domain.model

sealed class ResponseState<out T> {
    data class Success<T>(val data: T) : ResponseState<T>()
    data class Error(val error: com.jh.murun.domain.model.Error) : ResponseState<Nothing>()
}
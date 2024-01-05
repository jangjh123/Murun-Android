package com.jh.murun.domain.model

sealed class ResponseState<out T> {
    data class Success<T>(val data: T) : ResponseState<T>()
    data class Failure(val error: Error) : ResponseState<Nothing>()
}
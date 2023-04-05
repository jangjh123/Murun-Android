package com.jh.murun.data.remote

import com.jh.murun.data.model.response.ErrorResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class ResponseHandler {
    suspend fun <T> handle(call: suspend () -> Response<T>): Flow<MurunResponse<T>> = flow {
        val response = call.invoke()
        if (response.isSuccessful && response.body() != null) {
            emit(MurunResponse.Success(response.body()!!))
        } else {
            emit(MurunResponse.Error(ErrorResponse(code = response.code(), message = response.message())))
        }
    }
}
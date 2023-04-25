package com.jh.murun.data.repositoryImpl

import com.jh.murun.data.base.BaseResponse
import com.jh.murun.data.model.response.ErrorResponse
import com.jh.murun.data.remote.ApiService
import com.jh.murun.data.remote.MurunResponse
import com.jh.murun.data.remote.ResponseHandler
import com.jh.murun.domain.base.BaseModel
import com.jh.murun.domain.model.ResponseState
import com.jh.murun.domain.repository.GetMusicRepository
import kotlinx.coroutines.flow.*
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class GetMusicRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val responseHandler: ResponseHandler
) : GetMusicRepository {
    override suspend fun fetchMusicList(bpm: Int): Flow<ResponseState<BaseModel>> {
        return responseFlow(
            apiCall = apiService.fetchMusicList(bpm),
            downStreamErrorMessage = "1",
            upStreamErrorMessage = "2"
        )
    }

    override suspend fun fetchMusicById(id: String): Flow<ResponseState<BaseModel>> {
        return responseFlow(
            apiCall = apiService.fetchMusicById(id),
            downStreamErrorMessage = "3",
            upStreamErrorMessage = "4"
        )
    }

    override suspend fun fetchMusicFile(url: String): Flow<ResponseState<ResponseBody>> {
        return flow {
            responseHandler.handle {
                apiService.fetchMusicFile(url)
            }.onEach { result ->
                when (result) {
                    is MurunResponse.OnSuccess -> emit(ResponseState.OnSuccess(result.data))
                    is MurunResponse.OnError -> emit(ResponseState.OnError(ErrorResponse(message = "음원 파일 오류입니다.").toDataModel()))
                }
            }.catch {
                emit(ResponseState.OnError(ErrorResponse(message = "네트워크 연결 상태를 확인해 주세요.").toDataModel()))
            }.collect()
        }
    }

    override suspend fun fetchMusicImage(url: String): Flow<ResponseState<ResponseBody>> {
        return flow {
            responseHandler.handle {
                apiService.fetchMusicImage(url)
            }.onEach { result ->
                when (result) {
                    is MurunResponse.OnSuccess -> emit(ResponseState.OnSuccess(result.data))
                    is MurunResponse.OnError -> emit(ResponseState.OnError(ErrorResponse(message = "이미지 파일 오류입니다.").toDataModel()))
                }
            }.catch {
                emit(ResponseState.OnError(ErrorResponse(message = "네트워크 연결 상태를 확인해 주세요.").toDataModel()))
            }.collect()
        }
    }

    private suspend fun responseFlow(
        apiCall: Response<BaseResponse>,
        downStreamErrorCode: Int? = null,
        downStreamErrorMessage: String,
        upStreamErrorCode: Int? = null,
        upStreamErrorMessage: String
    ): Flow<ResponseState<BaseModel>> {
        return flow {
            responseHandler.handle {
                apiCall
            }.onEach { result ->
                when (result) {
                    is MurunResponse.OnSuccess -> emit(ResponseState.OnSuccess(result.data.toDataModel()))
                    is MurunResponse.OnError -> emit(ResponseState.OnError(result.error?.toDataModel() ?: ErrorResponse(code = downStreamErrorCode, message = downStreamErrorMessage).toDataModel()))
                }
            }.catch {
                emit(ResponseState.OnError(ErrorResponse(code = upStreamErrorCode, message = upStreamErrorMessage).toDataModel()))
            }.collect()
        }
    }
}
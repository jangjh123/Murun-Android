package com.jh.murun.data.repositoryImpl

import com.jh.murun.data.model.response.ErrorResponse
import com.jh.murun.data.model.response.ErrorResponse.Companion.toDataModel
import com.jh.murun.data.model.response.MusicResponse.Companion.toDataModel
import com.jh.murun.data.remote.ApiService
import com.jh.murun.data.remote.MurunResponse
import com.jh.murun.data.remote.ResponseHandler
import com.jh.murun.domain.model.Music
import com.jh.murun.domain.model.ResponseState
import com.jh.murun.domain.repository.GetMusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import okhttp3.ResponseBody
import javax.inject.Inject

class GetMusicRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val responseHandler: ResponseHandler
) : GetMusicRepository {

    override fun fetchMusicByBpm(bpm: Int): Flow<ResponseState<Music>> {
        return flow {
            responseHandler.handle {
                apiService.fetchMusicByBpm(bpm)
            }.onEach { result ->
                when (result) {
                    is MurunResponse.OnSuccess -> emit(ResponseState.Success(result.data.toDataModel()))
                    is MurunResponse.OnError -> emit(ResponseState.Failure(result.error.toDataModel()))
                }
            }.catch {
                emit(ResponseState.Failure(ErrorResponse(message = "네트워크 연결 상태를 확인해 주세요.").toDataModel()))
            }.collect()
        }
    }

    override fun fetchMusicImage(url: String): Flow<ResponseState<ResponseBody>> {
        return flow {
            responseHandler.handle {
                apiService.fetchMusicImage(url)
            }.onEach { result ->
                when (result) {
                    is MurunResponse.OnSuccess -> emit(ResponseState.Success(result.data))
                    is MurunResponse.OnError -> emit(ResponseState.Failure(ErrorResponse(message = "이미지 파일 오류입니다.").toDataModel()))
                }
            }.catch {
                emit(ResponseState.Failure(ErrorResponse(message = "네트워크 연결 상태를 확인해 주세요.").toDataModel()))
            }.collect()
        }
    }
}
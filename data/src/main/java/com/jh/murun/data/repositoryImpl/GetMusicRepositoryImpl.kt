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
import kotlinx.coroutines.flow.*
import okhttp3.ResponseBody
import javax.inject.Inject

class GetMusicRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val responseHandler: ResponseHandler
) : GetMusicRepository {

    override suspend fun fetchMusicListByBpm(bpm: Int): Flow<ResponseState<List<Music>>> {
        return flow {
            responseHandler.handle {
                apiService.fetchMusicList(bpm = bpm)
            }.onEach { result ->
                when (result) {
                    is MurunResponse.OnSuccess -> emit(ResponseState.Success(result.data.map { it.toDataModel() }))
                    is MurunResponse.OnError -> emit(ResponseState.Error(result.error.toDataModel()))
                }
            }.catch {
                emit(ResponseState.Error(ErrorResponse(message = "네트워크 연결 상태를 확인해 주세요.").toDataModel()))
            }.collect()
        }
    }

    override suspend fun fetchMusicImage(url: String): Flow<ResponseState<ResponseBody>> {
        return flow {
            responseHandler.handle {
                apiService.fetchMusicImage(url)
            }.onEach { result ->
                when (result) {
                    is MurunResponse.OnSuccess -> emit(ResponseState.Success(result.data))
                    is MurunResponse.OnError -> emit(ResponseState.Error(ErrorResponse(message = "이미지 파일 오류입니다.").toDataModel()))
                }
            }.catch {
                emit(ResponseState.Error(ErrorResponse(message = "네트워크 연결 상태를 확인해 주세요.").toDataModel()))
            }.collect()
        }
    }
}
package com.jh.murun.data.repositoryImpl

import com.jh.murun.data.model.response.ErrorResponse.Companion.toDataModel
import com.jh.murun.data.model.response.MusicResponse.Companion.toDataModel
import com.jh.murun.data.remote.ApiService
import com.jh.murun.data.remote.MurunResponse
import com.jh.murun.data.remote.ResponseHandler
import com.jh.murun.domain.model.Music
import com.jh.murun.domain.model.ResponseState
import com.jh.murun.domain.repository.GetMusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class GetMusicRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : GetMusicRepository {
    override suspend fun getMusicList(bpm: Int): Flow<ResponseState<List<Music>>> {
        return flow {
            ResponseHandler().handle {
                apiService.fetchMusicList(bpm = bpm)
            }.onEach { result ->
                when (result) {
                    is MurunResponse.Success -> emit(ResponseState.Success(result.data.map { it.toDataModel() }))
                    is MurunResponse.Error -> emit(ResponseState.Error(result.error.toDataModel()))
                }
            }.collect()
        }
    }
}
package com.jh.presentation.service.music_loader

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.jh.murun.domain.model.Music
import com.jh.murun.domain.model.ResponseState
import com.jh.murun.domain.use_case.favorite.GetFavoriteListUseCase
import com.jh.murun.domain.use_case.music.GetMusicImageUseCase
import com.jh.murun.domain.use_case.music.GetMusicListByBpmUseCase
import com.jh.presentation.di.IoDispatcher
import com.jh.presentation.di.MainDispatcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MusicLoaderService : Service() {
    @Inject
    @MainDispatcher
    lateinit var mainDispatcher: CoroutineDispatcher

    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    lateinit var getMusicListByBpmUseCase: GetMusicListByBpmUseCase

    @Inject
    lateinit var getFavoriteListUseCase: GetFavoriteListUseCase

    @Inject
    lateinit var getMusicImageUseCase: GetMusicImageUseCase

    private val _musicFlow: MutableSharedFlow<Music> = MutableSharedFlow(replay = Int.MAX_VALUE)
    val musicFlow: SharedFlow<Music>
        get() = _musicFlow

    inner class MusicLoaderServiceBinder : Binder() {
        fun getServiceInstance(): MusicLoaderService {
            return this@MusicLoaderService
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return MusicLoaderServiceBinder()
    }

    fun loadMusicListByBpm(bpm: Int) {
        CoroutineScope(ioDispatcher).launch {
            getMusicListByBpmUseCase(bpm = bpm).onEach { result ->
                when (result) {
                    is ResponseState.Success -> {
                        result.data.shuffled().forEach { music ->
                            music.imageUrl?.let { musicImageUrl ->
                                getMusicImageUseCase(musicImageUrl).collect { imageResult ->
                                    when (imageResult) {
                                        is ResponseState.Success -> {
                                            music.image = imageResult.data.bytes()
                                            withContext(mainDispatcher) {
                                                _musicFlow.emit(music)
                                            }
                                        }

                                        is ResponseState.Error -> {

                                        }
                                    }
                                }
                            }
                        }
                    }

                    is ResponseState.Error -> {
                        loadMusicListByBpm(130)
                    }
                }
            }.launchIn(CoroutineScope(ioDispatcher))
        }
    }

    fun loadFavoriteList() {
        CoroutineScope(ioDispatcher).launch {
            getFavoriteListUseCase().onEach { result ->
                result?.forEach { music ->
                    _musicFlow.emit(music)
                }
            }.launchIn(CoroutineScope(ioDispatcher))
        }
    }
}
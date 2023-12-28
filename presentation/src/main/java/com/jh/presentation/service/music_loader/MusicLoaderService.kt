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
import com.jh.presentation.di.MainImmediateDispatcher
import com.jh.presentation.enums.LoadingMusicType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MusicLoaderService : Service() {
    @Inject
    @MainImmediateDispatcher
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

    private var loadingMusicType: LoadingMusicType? = null

    inner class MusicLoaderServiceBinder : Binder() {
        fun getServiceInstance(): MusicLoaderService {
            return this@MusicLoaderService
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return MusicLoaderServiceBinder()
    }

    fun setLoadingMusicType(loadingMusicType: LoadingMusicType) {
        this.loadingMusicType = loadingMusicType
    }

    fun loadMusicListByBpm(bpm: Int) {
        CoroutineScope(ioDispatcher).launch {
            getMusicListByBpmUseCase(bpm).onEach { result ->
                when (result) {
                    is ResponseState.Success -> {
                        if (result.data.isEmpty()) {
                            loadMusicListByBpm(130)
                        } else {
                            if (loadingMusicType == LoadingMusicType.TRACKING_CADENCE) {
                                val music = result.data.shuffled().first()
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
                            } else {
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
                        }
                    }

                    is ResponseState.Error -> {

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
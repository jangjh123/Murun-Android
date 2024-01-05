package com.jh.presentation.service.music_loader

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import com.jh.murun.domain.model.Music
import com.jh.murun.domain.model.ResponseState.Failure
import com.jh.murun.domain.model.ResponseState.Success
import com.jh.murun.domain.use_case.favorite.GetFavoriteListUseCase
import com.jh.murun.domain.use_case.music.GetMusicByBpmUseCase
import com.jh.murun.domain.use_case.music.GetMusicImageUseCase
import com.jh.presentation.di.IoDispatcher
import com.jh.presentation.di.MainImmediateDispatcher
import com.jh.presentation.service.music_player.MusicPlayerStateManager.musicPlayerState
import com.jh.presentation.service.music_player.MusicPlayerStateManager.updateMusicPlayerState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MusicLoaderService : LifecycleService() {
    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    @MainImmediateDispatcher
    lateinit var mainImmediateDispatcher: CoroutineDispatcher

    @Inject
    lateinit var getMusicByBpmUseCase: GetMusicByBpmUseCase

    @Inject
    lateinit var getFavoriteListUseCase: GetFavoriteListUseCase

    @Inject
    lateinit var getMusicImageUseCase: GetMusicImageUseCase

    lateinit var exoPlayer: ExoPlayer

    inner class MusicLoaderServiceBinder : Binder() {
        fun getServiceInstance(): MusicLoaderService {
            return this@MusicLoaderService
        }
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return MusicLoaderServiceBinder()
    }

    fun loadMusicByBpm() {
        updateMusicPlayerState {
            it.copy(isLoading = true)
        }

        getMusicByBpmUseCase(musicPlayerState.value.cadence).onEach { result ->
            when (result) {
                is Success -> {
                    val music = result.data
                    music.imageUrl?.let { imageUrl ->
                        setImageToMusic(music, imageUrl)
                    } ?: run {
                        // todo : 이미지 없는 경우
                    }
                }

                is Failure -> {
                    when (result.error.code) {
                        STATUS_CODE_NO_MUSIC -> {
                            updateMusicPlayerState {
                                it.copy(cadence = INITIAL_CADENCE)
                            }

                            loadMusicByBpm()
                        }
                    }
                }
            }
        }.catch {
            // todo : 에러 핸들링
        }.launchIn(lifecycleScope + ioDispatcher)
    }

    private suspend fun setImageToMusic(
        music: Music,
        imageUrl: String
    ) {
        getMusicImageUseCase(imageUrl).onEach { result ->
            when (result) {
                is Success -> {
                    withContext(mainImmediateDispatcher) {
                        addMusic(
                            music = music.apply { image = result.data.bytes() },
                            isFavoriteList = false
                        )

                        updateMusicPlayerState {
                            it.copy(isLoading = false)
                        }
                    }
                }

                is Failure -> {
                    // todo : 에러 핸들링
                }
            }
        }.catch {
            // todo : 에러 핸들링
        }.collect()
    }

    private fun addMusic(
        music: Music,
        isFavoriteList: Boolean
    ) {
        val metadata = MediaMetadata.Builder()
            .setTitle(music.title)
            .setArtist(music.artist)
            .setArtworkData(music.image, MediaMetadata.PICTURE_TYPE_FRONT_COVER)
            .setExtras(bundleOf(Pair(METADATA_KEY_MUSIC, music)))
            .build()

        val mediaItem = MediaItem.Builder()
            .setUri(music.url)
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .setMediaMetadata(metadata)
            .build()

        exoPlayer.addMediaItem(mediaItem)

        if (!isFavoriteList) {
            exoPlayer.seekToNext()
        }
    }

    fun loadFavoriteList() {
        getFavoriteListUseCase().onEach { result ->
            result?.forEach { music ->
                addMusic(
                    music = music,
                    isFavoriteList = true
                )
            }
        }.catch {
            // todo : 에러 핸들링
        }.launchIn(lifecycleScope + ioDispatcher)
    }

    companion object {
        private const val METADATA_KEY_MUSIC = "music"
        private const val INITIAL_CADENCE = 130
        private const val STATUS_CODE_NO_MUSIC = 500
    }
}
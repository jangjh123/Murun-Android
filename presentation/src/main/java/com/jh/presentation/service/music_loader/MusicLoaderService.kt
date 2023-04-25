package com.jh.presentation.service.music_loader

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.jh.murun.domain.model.Music
import com.jh.murun.domain.model.ResponseState
import com.jh.murun.domain.use_case.favorite.GetFavoriteListUseCase
import com.jh.murun.domain.use_case.favorite.GetFavoriteMusicByIdUseCase
import com.jh.murun.domain.use_case.music.GetMusicByIdUseCase
import com.jh.murun.domain.use_case.music.GetMusicFileUseCase
import com.jh.murun.domain.use_case.music.GetMusicImageUseCase
import com.jh.murun.domain.use_case.music.GetMusicListByCadenceUseCase
import com.jh.presentation.di.IoDispatcher
import com.jh.presentation.di.MainDispatcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MusicLoaderService : Service() {
    @Inject
    lateinit var getMusicByIdUseCase: GetMusicByIdUseCase

    @Inject
    lateinit var getMusicListByCadenceUseCase: GetMusicListByCadenceUseCase

    @Inject
    lateinit var getFavoriteListUseCase: GetFavoriteListUseCase

    @Inject
    lateinit var getMusicFileUseCase: GetMusicFileUseCase

    @Inject
    lateinit var getMusicImageUseCase: GetMusicImageUseCase

    @Inject
    lateinit var getFavoriteMusicByIdUseCase: GetFavoriteMusicByIdUseCase

    @Inject
    @MainDispatcher
    lateinit var mainDispatcher: CoroutineDispatcher

    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    private val _completeMusicFlow: MutableSharedFlow<Music?> = MutableSharedFlow()
    val completeMusicFlow: SharedFlow<Music?>
        get() = _completeMusicFlow

    private val musicQueue: Queue<Music> = LinkedList()

    inner class MusicLoaderServiceBinder : Binder() {
        fun getServiceInstance(): MusicLoaderService {
            return this@MusicLoaderService
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return MusicLoaderServiceBinder()
    }

    fun loadMusicListByCadence(cadence: Int) {
        CoroutineScope(ioDispatcher).launch {
            getMusicListByCadenceUseCase(cadence = cadence).onEach { result ->
                when (result) {
                    is ResponseState.Success -> {
                        musicQueue.clear()
                        musicQueue.addAll(
                            listOf(
                                result.data.first(),
                                Music(
                                    id = "aaba",
                                    artist = "d",
                                    duration = 0L,
                                    imageUrl = "https://i.stack.imgur.com/kPTSA.jpg?s=256&g=1",
                                    fileUrl = "https://cdn.pixabay.com/download/audio/2023/03/26/audio_87449b1afe.mp3?filename=mortal-gaming-144000.mp3",
                                    title = "타이틀",
                                    isStored = false
                                )
                            ).shuffled()
                        )

                        if (musicQueue.isNotEmpty()) {
                            loadMusicFileAndImage(musicQueue.poll()!!)
                        } else {
                            // TODO : NoMusic Error Handling
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
                if (result != null) {
                    musicQueue.addAll(result)
                    if (musicQueue.isNotEmpty()) {
                        loadMusicFileAndImage(musicQueue.poll()!!)
                    }
                } else {
                    // TODO : Error Handling
                }
            }.launchIn(CoroutineScope(ioDispatcher))
        }
    }

    private fun loadMusicFileAndImage(music: Music) {
        if (music.diskPath != null) {
            CoroutineScope(ioDispatcher).launch {
                if (music.diskPath != null) {
                    _completeMusicFlow.emit(music.apply {
                        diskPath = writeMusicFileToCache(File(music.diskPath!!).inputStream(), music.title)
                    })
                }
            }

            return
        }

        CoroutineScope(ioDispatcher).launch {
            getFavoriteMusicByIdUseCase(music.id).onEach { result ->
                if (result != null) {
                    _completeMusicFlow.emit(result.apply {
                        diskPath = writeMusicFileToCache(File(result.diskPath!!).inputStream(), result.title)
                    })
                } else {
                    getMusicFileUseCase(music.fileUrl!!).zip(getMusicImageUseCase(music.imageUrl!!)) { musicFile, imageFile ->
                        if (musicFile is ResponseState.Success && imageFile is ResponseState.Success) {
                            return@zip Pair(writeMusicFileToCache(musicFile.data.byteStream(), music.title), imageFile.data.bytes())
                        } else if (musicFile is ResponseState.Success && imageFile is ResponseState.Error) {
                            return@zip Pair(writeMusicFileToCache(musicFile.data.byteStream(), music.title), null)
                        } else {
                            return@zip null
                        }
                    }.onEach { pair ->
                        if (pair != null) {
                            _completeMusicFlow.emit(music.apply {
                                diskPath = pair.first
                                image = pair.second
                            })
                        } else {
                            // TODO : Error Handling
                        }
                    }.launchIn(CoroutineScope(ioDispatcher))
                }
            }.launchIn(CoroutineScope(ioDispatcher))
        }
    }

    private suspend fun writeMusicFileToCache(byteStream: InputStream, title: String): String {
        var path = ""
        try {
            val file = File(applicationContext.cacheDir, "$title.mp3")
            val byteArray = ByteArray(4096)
            var fileSizeDownloaded = 0
            withContext(ioDispatcher) {
                val outputStream = FileOutputStream(file)

                while (true) {
                    val read = byteStream.read(byteArray)

                    if (read == -1) {
                        break
                    }

                    outputStream.write(byteArray, 0, read)
                    fileSizeDownloaded += read
                    outputStream.flush()
                }
            }

            path = file.absolutePath
        } catch (e: Exception) {
            println(e)
        }

        return path
    }

    fun loadNextMusicFile() {
        if (musicQueue.isNotEmpty()) {
            loadMusicFileAndImage(musicQueue.poll()!!)
        } else {
            // TODO : NoSuchException Handling
        }
    }
}
package com.jh.presentation.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.jh.murun.domain.model.MusicInfo
import com.jh.murun.domain.model.ResponseState
import com.jh.murun.domain.use_case.music.GetMusicFileUseCase
import com.jh.murun.domain.use_case.music.GetMusicInfoByIdUseCase
import com.jh.murun.domain.use_case.music.GetMusicInfoListByCadenceUseCase
import com.jh.presentation.di.IoDispatcher
import com.jh.presentation.di.MainDispatcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
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
    lateinit var getMusicInfoByIdUseCase: GetMusicInfoByIdUseCase

    @Inject
    lateinit var getMusicInfoListByCadenceUseCase: GetMusicInfoListByCadenceUseCase

    @Inject
    lateinit var getMusicFileUseCase: GetMusicFileUseCase

    @Inject
    @MainDispatcher
    lateinit var mainDispatcher: CoroutineDispatcher

    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    private val _completeMusicFlow: MutableSharedFlow<MusicInfo> = MutableSharedFlow()
    val completeMusicFlow: SharedFlow<MusicInfo>
        get() = _completeMusicFlow

    private val musicQueue: Queue<MusicInfo> = LinkedList()

    inner class MusicLoaderServiceBinder : Binder() {
        fun getServiceInstance(): MusicLoaderService {
            return this@MusicLoaderService
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return MusicLoaderServiceBinder()
    }

    fun loadMusicInfoListByCadence(cadence: Int) {
        CoroutineScope(ioDispatcher).launch {
            getMusicInfoListByCadenceUseCase(cadence = cadence).onEach { result ->
                when (result) {
                    is ResponseState.Success -> {
                        musicQueue.clear()
                        musicQueue.addAll(
                            listOf(
                                result.data.first(),
                                MusicInfo(
                                    uuid = "",
                                    artist = "d",
                                    albumImage = null,
                                    bpm = 130,
                                    diskPath = null,
                                    url = "https://cdn.pixabay.com/download/audio/2023/03/26/audio_87449b1afe.mp3?filename=mortal-gaming-144000.mp3",
                                    title = "타이틀"
                                )
                            )
                        )

                        if (musicQueue.isNotEmpty()) {
                            loadMusicFile(musicQueue.poll()!!)
                        }
                    }
                    is ResponseState.Error -> {} // TODO : Error handling
                }
            }.collect()
        }
    }

    private fun loadMusicFile(musicInfo: MusicInfo) {
        CoroutineScope(ioDispatcher).launch {
            getMusicFileUseCase(url = musicInfo.url).onEach { result ->
                if (result != null) {
                    _completeMusicFlow.emit(
                        musicInfo.apply {
                            diskPath = writeFileToDisk(result.byteStream(), musicInfo.title)
                        }
                    )
                } else {
                    // TODO : Error Handling
                }
            }.collect()
        }
    }

    private suspend fun writeFileToDisk(byteStream: InputStream, title: String): String {
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
            loadMusicFile(musicQueue.poll()!!)
        } else {
            // TODO : NoSuchException Handling
        }
    }
}
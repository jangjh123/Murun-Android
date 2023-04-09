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

    private val musicInfoQueue: Queue<MusicInfo> = LinkedList()

    inner class MusicLoaderServiceBinder : Binder() {
        fun getServiceInstance(): MusicLoaderService {
            return this@MusicLoaderService
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return MusicLoaderServiceBinder()
    }

    fun loadMusicWithCadence(
        cadence: Int,
        onSuccess: (Queue<MusicInfo>) -> Unit
    ) {
        CoroutineScope(ioDispatcher).launch {
            getMusicInfoListByCadenceUseCase(cadence = cadence).onEach { result ->
                when (result) {
                    is ResponseState.Success -> {
                        musicInfoQueue.clear()
                        musicInfoQueue.addAll(result.data)
                        onSuccess(musicInfoQueue)
                    }
                    is ResponseState.Error -> {} // TODO : Error handling
                }
            }.collect()
        }
    }

    fun loadMusicFile(
        musicInfo: MusicInfo,
        onWrittenToDisk: (String) -> Unit
    ) {
        CoroutineScope(ioDispatcher).launch {
            getMusicFileUseCase(url = musicInfo.musicUrl).onEach { result ->
                if (result != null) {
                    withContext(mainDispatcher) {
                        onWrittenToDisk(writeFileToDisk(result.byteStream()))
                    }
                } else {
                    // TODO : Error Handling
                }
            }.collect()
        }
    }

    private fun writeFileToDisk(byteStream: InputStream): String {
        var path = ""
        try {
            val file = File(applicationContext.cacheDir, "music.mp3")
            val byteArray = ByteArray(4096)
            var fileSizeDownloaded = 0
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

            path = file.absolutePath
        } catch (e: java.lang.Exception) {
            println(e)
        }

        return path
    }
}
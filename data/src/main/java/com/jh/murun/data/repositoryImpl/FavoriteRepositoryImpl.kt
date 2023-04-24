package com.jh.murun.data.repositoryImpl

import android.content.Context
import android.os.Environment
import com.jh.murun.data.local.MusicDao
import com.jh.murun.domain.model.Music
import com.jh.murun.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val musicDao: MusicDao,
    private val context: Context
) : FavoriteRepository {

    override suspend fun readAllMusics(): Flow<List<Music>?> {
        return flow {
            runCatching {
                musicDao.readAllMusic()
            }.onSuccess {
                emit(it)
            }.onFailure {
                emit(null)
            }
        }
    }

    override suspend fun insertMusicToFavoriteList(music: Music): Flow<Boolean> {
        val cacheFile = File(music.diskPath!!)
        val externalStorageFile = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.absolutePath + File.separator + "${music.artist} - ${music.title}.mp3")

        cacheFile.copyTo(externalStorageFile)
        music.diskPath = externalStorageFile.absolutePath

        return flow {
            runCatching {
                musicDao.insertMusic(music)
            }.onSuccess {
                emit(true)
            }.onFailure {
                emit(false)
            }
        }
    }

    override suspend fun isMusicExistsInFavoriteList(id: String): Flow<Boolean> {
        return flow {
            runCatching {
                musicDao.readMusicId(id)
            }.onSuccess {
                when(it) {
                    true -> {
                        emit(true)
                    }
                    false -> {
                        emit(false)
                    }
                }
            }.onFailure {
                emit(false)
            }
        }
    }

    override suspend fun deleteMusicFromFavoriteList(id: String): Flow<Boolean> {
        return flow {
            runCatching {
                musicDao.deleteMusic(id)
            }.onSuccess {
                emit(true)
            }.onFailure {
                emit(false)
            }
        }
    }
}
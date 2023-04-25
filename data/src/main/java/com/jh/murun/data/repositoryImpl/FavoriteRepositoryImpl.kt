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

    override suspend fun getMusicById(id: String): Flow<Music?> {
        return flow {
            runCatching {
                musicDao.readMusic(id)
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

        try {
            cacheFile.copyTo(externalStorageFile, true)
            music.apply {
                diskPath = externalStorageFile.absolutePath
                isStored = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

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

    override suspend fun deleteMusicFromFavoriteList(music: Music): Flow<Boolean> {
        try {
            File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.absolutePath + File.separator + "${music.artist} - ${music.title}.mp3").delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return flow {
            runCatching {
                musicDao.deleteMusic(music.id)
            }.onSuccess {
                emit(true)
            }.onFailure {
                emit(false)
            }
        }
    }

    override suspend fun updateReorderedFavoriteList(musics: List<Music>) {
        runCatching {
            musicDao.deleteAllMusic()
        }.onSuccess {
            musicDao.insertAllMusic(musics)
        }
    }
}
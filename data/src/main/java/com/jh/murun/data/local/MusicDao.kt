package com.jh.murun.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jh.murun.domain.model.Music

@Dao
interface MusicDao {
    @Query("SELECT * FROM Music")
    suspend fun readAllMusic(): List<Music>

    @Query("SELECT * FROM Music WHERE id = :id")
    suspend fun readMusic(id: String): Music

    @Query("SELECT EXISTS (SELECT 1 FROM Music WHERE id = :id) AS result")
    suspend fun readMusicId(id: String): Boolean

    @Insert
    suspend fun insertAllMusic(musics: List<Music>)

    @Insert
    suspend fun insertMusic(music: Music)

    @Query("DELETE FROM Music WHERE id = :id")
    suspend fun deleteMusic(id: String)

    @Query("DELETE FROM Music")
    suspend fun deleteAllMusic()
}
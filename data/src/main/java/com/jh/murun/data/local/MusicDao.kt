package com.jh.murun.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jh.murun.domain.model.Music

@Dao
interface MusicDao {
    @Query("SELECT * FROM Music ORDER BY newIndex")
    suspend fun readAllMusic(): List<Music>

    @Insert
    suspend fun insertAllMusic(musics: List<Music>)

    @Insert
    suspend fun insertMusic(music: Music)

    @Query("DELETE FROM Music WHERE id = :id")
    suspend fun deleteMusic(id: Int)

    @Query("DELETE FROM Music")
    suspend fun deleteAllMusic()
}
package com.jh.murun.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jh.murun.domain.model.Music
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {
    @Query("SELECT * FROM Music")
    fun readAllMusic(): Flow<List<Music>>

    @Insert
    fun insertMusic(music: Music)

    @Query("DELETE FROM Music WHERE id = :id")
    fun deleteMusic(id: String)
}
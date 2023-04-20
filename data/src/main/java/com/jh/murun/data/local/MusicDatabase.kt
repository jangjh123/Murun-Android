package com.jh.murun.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jh.murun.domain.model.Music

@Database(entities = [Music::class], version = 0, exportSchema = false)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun getMusicDao(): MusicDao

    companion object {
        const val DATABASE_NAME = "music_db"
    }
}
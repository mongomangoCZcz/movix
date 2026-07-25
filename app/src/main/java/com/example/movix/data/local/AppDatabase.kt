package com.example.movix.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WatchProgress::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watchProgressDao(): WatchProgressDao
}

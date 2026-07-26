package com.example.movix.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchProgressDao {
    @Query("SELECT * FROM watch_progress ORDER BY lastWatched DESC")
    fun getAllWatchProgress(): Flow<List<WatchProgress>>

    @Query("SELECT * FROM watch_progress WHERE id = :id LIMIT 1")
    suspend fun getWatchProgress(id: Int): WatchProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchProgress(watchProgress: WatchProgress)

    @Query("DELETE FROM watch_progress WHERE id = :id")
    suspend fun deleteWatchProgress(id: Int)
}

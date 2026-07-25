package com.example.movix.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watch_progress")
data class WatchProgress(
    @PrimaryKey val id: Int, // TMDB ID
    val title: String,
    val mediaType: String,
    val posterPath: String?,
    val position: Long,
    val duration: Long,
    val lastWatched: Long = System.currentTimeMillis(),
    val season: Int? = null,
    val episode: Int? = null,
    val fileIdent: String? = null,
    val fileName: String? = null
)

package com.example.movix.data.remote

import com.example.movix.data.remote.model.VpsStream
import retrofit2.http.GET
import retrofit2.http.Query

interface VpsApiService {
    @GET("/")
    suspend fun getStreams(
        @Query("tmdb_id") tmdbId: Int,
        @Query("media_type") mediaType: String,
        @Query("season") season: Int? = null,
        @Query("episode") episode: Int? = null
    ): List<VpsStream>
}

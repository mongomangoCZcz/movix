package com.example.movix.data.remote

import com.example.movix.data.remote.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {
    @GET("3/search/multi")
    suspend fun searchMulti(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String = "cs-CZ"
    ): TmdbSearchResponse

    @GET("3/discover/{media_type}")
    suspend fun discover(
        @Path("media_type") mediaType: String,
        @Query("api_key") apiKey: String,
        @Query("with_genres") genreId: Int?,
        @Query("language") language: String = "cs-CZ",
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("page") page: Int = 1
    ): TmdbSearchResponse

    @GET("3/genre/{media_type}/list")
    suspend fun getGenres(
        @Path("media_type") mediaType: String,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "cs-CZ"
    ): TmdbGenreResponse

    @GET("3/tv/{tv_id}")
    suspend fun getTvDetails(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "cs-CZ"
    ): TmdbTvDetailResponse

    @GET("3/tv/{tv_id}/season/{season_number}")
    suspend fun getSeasonDetails(
        @Path("tv_id") tvId: Int,
        @Path("season_number") seasonNumber: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "cs-CZ"
    ): TmdbSeasonDetailResponse

    @GET("3/movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "cs-CZ"
    ): TmdbMediaItem

    @GET("3/trending/all/day")
    suspend fun getTrending(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "cs-CZ",
        @Query("page") page: Int = 1
    ): TmdbSearchResponse
}

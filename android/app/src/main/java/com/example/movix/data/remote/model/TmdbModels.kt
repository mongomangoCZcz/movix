package com.example.movix.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TmdbSearchResponse(
    val results: List<TmdbMediaItem>
)

@JsonClass(generateAdapter = true)
data class TmdbMediaItem(
    val id: Int,
    @Json(name = "media_type") val mediaType: String?,
    val title: String?,
    val name: String?,
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "first_air_date") val firstAirDate: String?,
    val overview: String?,
    @Json(name = "poster_path") val posterPath: String?,
    val runtime: Int?
)

@JsonClass(generateAdapter = true)
data class TmdbGenreResponse(
    val genres: List<TmdbGenre>
)

@JsonClass(generateAdapter = true)
data class TmdbGenre(
    val id: Int,
    val name: String
)

@JsonClass(generateAdapter = true)
data class TmdbTvDetailResponse(
    val seasons: List<TmdbSeason>,
    @Json(name = "poster_path") val posterPath: String?
)

@JsonClass(generateAdapter = true)
data class TmdbSeason(
    @Json(name = "season_number") val seasonNumber: Int,
    val name: String?,
    @Json(name = "poster_path") val posterPath: String?,
    val overview: String?
)

@JsonClass(generateAdapter = true)
data class TmdbSeasonDetailResponse(
    val episodes: List<TmdbEpisode>
)

@JsonClass(generateAdapter = true)
data class TmdbEpisode(
    @Json(name = "episode_number") val episodeNumber: Int,
    val name: String?,
    val overview: String?,
    @Json(name = "still_path") val stillPath: String?,
    val runtime: Int?
)

package com.example.movix.data.repository

import com.example.movix.data.local.WatchProgress
import com.example.movix.data.local.WatchProgressDao
import com.example.movix.data.remote.TmdbApiService
import com.example.movix.data.remote.VpsApiService
import com.example.movix.data.remote.WebshareApiService
import com.example.movix.data.remote.model.TmdbMediaItem
import com.example.movix.data.remote.model.WebshareFile
import com.example.movix.domain.utils.FilenameAnalyzer
import java.text.Normalizer
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepository @Inject constructor(
    private val tmdbApi: TmdbApiService,
    private val webshareApi: WebshareApiService,
    private val vpsApi: VpsApiService,
    private val watchProgressDao: WatchProgressDao
) {
    private val TMDB_API_KEY = "8badb6099e4a0e44f009dac72c4df37a"

    suspend fun saveWatchProgress(watchProgress: WatchProgress) = 
        watchProgressDao.insertWatchProgress(watchProgress)

    fun getAllWatchProgress() = watchProgressDao.getAllWatchProgress()

    suspend fun getWatchProgress(id: Int) = watchProgressDao.getWatchProgress(id)

    suspend fun deleteWatchProgress(id: Int) = watchProgressDao.deleteWatchProgress(id)

    suspend fun searchTmdMilti(query: String) = tmdbApi.searchMulti(TMDB_API_KEY, query)

    suspend fun getGenres(mediaType: String) = tmdbApi.getGenres(mediaType, TMDB_API_KEY)

    suspend fun discover(mediaType: String, genreId: Int?, page: Int = 1) = 
        tmdbApi.discover(mediaType, TMDB_API_KEY, genreId, page = page)

    suspend fun getTrending(page: Int = 1) = tmdbApi.getTrending(TMDB_API_KEY, page = page)

    suspend fun getTvDetails(tvId: Int) = tmdbApi.getTvDetails(tvId, TMDB_API_KEY)

    suspend fun getSeasonDetails(tvId: Int, seasonNumber: Int) = 
        tmdbApi.getSeasonDetails(tvId, seasonNumber, TMDB_API_KEY)

    suspend fun getStreams(
        tmdbId: Int,
        title: String,
        mediaType: String,
        year: String? = null,
        season: Int? = null,
        episode: Int? = null
    ): List<WebshareFile> {
        val streams = mutableListOf<WebshareFile>()

        // 1. VPS
        try {
            val vpsStreams = vpsApi.getStreams(tmdbId, mediaType, season, episode)
            streams.addAll(vpsStreams.map { WebshareFile(it.ident, it.name, it.size) })
        } catch (e: Exception) {
            // Log VPS error
        }

        if (streams.isNotEmpty()) return streams

        // 2. Webshare Fallback
        val queries = if (season != null && episode != null) {
            listOf(
                "$title S${"%02d".format(season)}E${"%02d".format(episode)}",
                "$title ${season}x${"%02d".format(episode)}",
                title
            )
        } else {
            if (year != null) listOf("$title $year", title) else listOf(title)
        }

        for (q in queries) {
            try {
                val response = webshareApi.search(q)
                if (response.status == "OK" && response.files != null) {
                    val filtered = response.files.filter { file ->
                        validateFilename(file.name, title, season, episode)
                    }
                    if (filtered.isNotEmpty()) return filtered
                }
            } catch (e: Exception) {
                // Log Webshare error
            }
        }

        return emptyList()
    }

    suspend fun getFileLink(ident: String, token: String): Result<String> {
        return try {
            val response = webshareApi.getFileLink(ident, token)
            if (response.status == "OK" && response.link != null) {
                Result.success(response.link)
            } else {
                Result.failure(Exception(response.message ?: "Failed to get link"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun validateFilename(filename: String, title: String, season: Int?, episode: Int?): Boolean {
        val normFile = normalizeString(filename)
        val normTitleWords = normalizeString(title).split(" ").filter { it.length > 1 }
        
        for (word in normTitleWords) {
            if (!normFile.contains(word)) return false
        }

        if (season != null && episode != null) {
            val sE = "s${"%02d".format(season)}e${"%02d".format(episode)}"
            val x1 = "${season}x${"%02d".format(episode)}"
            val x2 = "${"%02d".format(season)}x${"%02d".format(episode)}"
            val normNoSpaces = normFile.replace(" ", "")
            if (!normNoSpaces.contains(sE) && !normNoSpaces.contains(x1) && !normNoSpaces.contains(x2)) {
                return false
            }
        }
        return true
    }

    private fun normalizeString(text: String): String {
        val normalized = Normalizer.normalize(text, Normalizer.Form.NFKD)
        return normalized.replace("[^\\p{ASCII}]".toRegex(), "")
            .lowercase(Locale.ROOT)
            .replace("[^a-z0-9]".toRegex(), " ")
            .trim()
    }
}

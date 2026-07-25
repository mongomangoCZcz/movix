package com.example.movix.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movix.data.local.WatchProgress
import com.example.movix.data.remote.model.*
import com.example.movix.data.repository.MediaRepository
import com.example.movix.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<TmdbMediaItem>>(emptyList())
    val searchResults: StateFlow<List<TmdbMediaItem>> = _searchResults

    private val _genres = MutableStateFlow<List<TmdbGenre>>(emptyList())
    val genres: StateFlow<List<TmdbGenre>> = _genres

    private val _streams = MutableStateFlow<List<WebshareFile>>(emptyList())
    val streams: StateFlow<List<WebshareFile>> = _streams

    private val _trendingContent = MutableStateFlow<List<TmdbMediaItem>>(emptyList())
    val trendingContent: StateFlow<List<TmdbMediaItem>> = _trendingContent

    private val _featuredItem = MutableStateFlow<TmdbMediaItem?>(null)
    val featuredItem: StateFlow<TmdbMediaItem?> = _featuredItem

    private val _continueWatching = MutableStateFlow<List<WatchProgress>>(emptyList())
    val continueWatching: StateFlow<List<WatchProgress>> = _continueWatching

    private val _playUrl = MutableStateFlow<String?>(null)
    val playUrl: StateFlow<String?> = _playUrl

    val username: StateFlow<String?> = preferencesRepository.username.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun playStream(ident: String) {
        viewModelScope.launch {
            try {
                val token = preferencesRepository.wstToken.first()
                if (token == null) {
                    // Handle no token
                    return@launch
                }
                val result = mediaRepository.getFileLink(ident, token)
                result.onSuccess {
                    _playUrl.value = it
                }.onFailure {
                    // Handle failure
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun onPlayerNavigated() {
        _playUrl.value = null
    }

    suspend fun getTvDetails(tvId: Int) = mediaRepository.getTvDetails(tvId)
    suspend fun getSeasonDetails(tvId: Int, seasonNumber: Int) = mediaRepository.getSeasonDetails(tvId, seasonNumber)

    fun search(query: String) {
        viewModelScope.launch {
            try {
                val response = mediaRepository.searchTmdMilti(query)
                _searchResults.value = response.results.filter { it.mediaType == "movie" || it.mediaType == "tv" }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun loadGenres(mediaType: String) {
        viewModelScope.launch {
            try {
                val response = mediaRepository.getGenres(mediaType)
                _genres.value = response.genres
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun loadTrending() {
        viewModelScope.launch {
            try {
                val response = mediaRepository.getTrending()
                val items = response.results.filter { it.mediaType == "movie" || it.mediaType == "tv" }
                _trendingContent.value = items
                if (items.isNotEmpty() && _featuredItem.value == null) {
                    _featuredItem.value = items.random()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun loadWatchProgress() {
        viewModelScope.launch {
            mediaRepository.getAllWatchProgress().collect {
                _continueWatching.value = it
            }
        }
    }

    fun saveWatchProgress(progress: WatchProgress) {
        viewModelScope.launch {
            mediaRepository.saveWatchProgress(progress)
        }
    }

    suspend fun getWatchProgress(id: Int) = mediaRepository.getWatchProgress(id)

    fun discover(mediaType: String, genreId: Int?) {
        viewModelScope.launch {
            try {
                val response = mediaRepository.discover(mediaType, genreId)
                _searchResults.value = response.results
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun getStreams(
        tmdbId: Int,
        title: String,
        mediaType: String,
        year: String? = null,
        season: Int? = null,
        episode: Int? = null
    ) {
        viewModelScope.launch {
            try {
                val results = mediaRepository.getStreams(tmdbId, title, mediaType, year, season, episode)
                _streams.value = results
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

package com.example.movix.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.movix.data.remote.model.TmdbEpisode
import com.example.movix.data.remote.model.TmdbSeason
import com.example.movix.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonsScreen(navController: NavController, tvId: Int, title: String, viewModel: MediaViewModel = hiltViewModel()) {
    var seasons by remember { mutableStateOf<List<TmdbSeason>>(emptyList()) }

    LaunchedEffect(tvId) {
        try {
            seasons = viewModel.getTvDetails(tvId).seasons.filter { it.seasonNumber > 0 }
        } catch (e: Exception) {}
    }

    Scaffold(topBar = { TopAppBar(title = { Text(title) }) }) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(seasons) { season ->
                Card(Modifier.fillMaxWidth().clickable { navController.navigate(Screen.Episodes.createRoute(tvId, season.seasonNumber, title)) }) {
                    Row(Modifier.padding(8.dp)) {
                        AsyncImage(model = "https://image.tmdb.org/t/p/w200${season.posterPath}", contentDescription = null, modifier = Modifier.width(60.dp).height(90.dp))
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(season.name ?: "Season ${season.seasonNumber}", style = MaterialTheme.typography.titleMedium)
                            Text(season.overview ?: "", maxLines = 2, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodesScreen(navController: NavController, tvId: Int, season: Int, title: String, viewModel: MediaViewModel = hiltViewModel()) {
    var episodes by remember { mutableStateOf<List<TmdbEpisode>>(emptyList()) }
    var posterPath by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(tvId, season) {
        try {
            val tvDetails = viewModel.getTvDetails(tvId)
            posterPath = tvDetails.posterPath
            episodes = viewModel.getSeasonDetails(tvId, season).episodes
        } catch (e: Exception) {}
    }

    Scaffold(topBar = { TopAppBar(title = { Text("$title - Season $season") }) }) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(episodes) { episode ->
                Card(Modifier.fillMaxWidth().clickable {
                    navController.navigate(Screen.Streams.createRoute(tvId, title, "tv", null, season, episode.episodeNumber, posterPath))
                }) {
                    Row(Modifier.padding(8.dp)) {
                        AsyncImage(model = "https://image.tmdb.org/t/p/w200${episode.stillPath}", contentDescription = null, modifier = Modifier.width(100.dp).height(60.dp))
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("${episode.episodeNumber}. ${episode.name}", style = MaterialTheme.typography.titleMedium)
                            Text(episode.overview ?: "", maxLines = 2, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

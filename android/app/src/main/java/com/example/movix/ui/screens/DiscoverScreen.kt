package com.example.movix.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.movix.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    navController: NavController,
    mediaType: String,
    genreId: Int?,
    genreName: String,
    viewModel: MediaViewModel = hiltViewModel()
) {
    val results by viewModel.searchResults.collectAsState()

    LaunchedEffect(genreId) {
        viewModel.discover(mediaType, genreId)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(genreName) }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(results) { item ->
                MediaItemRow(item) {
                    if (mediaType == "movie") {
                        navController.navigate(Screen.Streams.createRoute(
                            item.id, item.title ?: "", "movie", item.releaseDate?.take(4), null, null, item.posterPath
                        ))
                    } else {
                        navController.navigate(Screen.Seasons.createRoute(item.id, item.name ?: ""))
                    }
                }
            }
        }
    }
}

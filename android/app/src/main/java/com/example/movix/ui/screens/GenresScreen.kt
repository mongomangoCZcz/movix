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
import com.example.movix.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenresScreen(navController: NavController, mediaType: String, viewModel: MediaViewModel = hiltViewModel()) {
    val genres by viewModel.genres.collectAsState()

    LaunchedEffect(mediaType) {
        viewModel.loadGenres(mediaType)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Žánry: ${if (mediaType == "movie") "Filmy" else "Seriály"}") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(genres) { genre ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        navController.navigate(Screen.Discover.createRoute(mediaType, genre.id, genre.name))
                    }
                ) {
                    Text(genre.name, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

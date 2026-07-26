package com.example.movix.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.movix.data.remote.model.TmdbMediaItem
import com.example.movix.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, viewModel: MediaViewModel = hiltViewModel()) {
    var query by remember { mutableStateOf("") }
    val results by viewModel.searchResults.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = {
                TextField(
                    value = query,
                    onValueChange = { 
                        query = it
                        if (it.length > 2) viewModel.search(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Hledat...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true
                )
            })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(results) { item ->
                MediaItemRow(item) {
                    if (item.mediaType == "movie") {
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

@Composable
fun MediaItemRow(item: TmdbMediaItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w200${item.posterPath}",
                contentDescription = null,
                modifier = Modifier.width(80.dp).height(120.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                val title = item.title ?: item.name ?: "Unknown"
                val year = (item.releaseDate ?: item.firstAirDate ?: "").take(4)
                Text(if (year.isNotEmpty()) "$title ($year)" else title, style = MaterialTheme.typography.titleMedium)
                Text(item.overview ?: "", maxLines = 3, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

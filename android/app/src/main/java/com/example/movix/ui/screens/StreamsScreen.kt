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
import com.example.movix.data.remote.model.WebshareFile
import com.example.movix.domain.utils.FilenameAnalyzer
import com.example.movix.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamsScreen(
    navController: NavController,
    tmdbId: Int,
    title: String,
    mediaType: String,
    year: String?,
    season: Int?,
    episode: Int?,
    posterPath: String?,
    viewModel: MediaViewModel = hiltViewModel()
) {
    val streams by viewModel.streams.collectAsState()
    val playUrl by viewModel.playUrl.collectAsState()
    var selectedFileName by remember { mutableStateOf("") }
    var selectedFileIdent by remember { mutableStateOf<String?>(null) }
    var isLoadingLink by remember { mutableStateOf(false) }

    LaunchedEffect(tmdbId) {
        viewModel.getStreams(tmdbId, title, mediaType, year, season, episode)
    }

    LaunchedEffect(playUrl) {
        playUrl?.let { url ->
            isLoadingLink = false
            navController.navigate(
                Screen.Player.createRoute(
                    url, 
                    selectedFileName, 
                    tmdbId, 
                    mediaType, 
                    posterPath, 
                    season, 
                    episode,
                    selectedFileIdent
                )
            )
            viewModel.onPlayerNavigated()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Zdroje: $title") }) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (streams.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(streams) { file ->
                        StreamRow(file) {
                            selectedFileName = file.name
                            selectedFileIdent = file.ident
                            isLoadingLink = true
                            viewModel.playStream(file.ident)
                        }
                    }
                }
            }

            if (isLoadingLink) {
                AlertDialog(
                    onDismissRequest = { isLoadingLink = false },
                    title = { Text("Získávání odkazu") },
                    text = {
                        Box(Modifier.fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    },
                    confirmButton = {}
                )
            }
        }
    }
}

@Composable
fun StreamRow(file: WebshareFile, onClick: () -> Unit) {
    val metadata = FilenameAnalyzer.analyze(file.name)
    val sizeStr = formatSize(file.size ?: 0)
    
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(file.name, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuggestionChip(onClick = {}, label = { Text(metadata.quality) })
                SuggestionChip(onClick = {}, label = { Text(metadata.codec) })
                SuggestionChip(onClick = {}, label = { Text(metadata.languages) })
                Spacer(Modifier.weight(1f))
                Text(sizeStr, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

fun formatSize(sizeBytes: Long): String {
    if (sizeBytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val i = (Math.floor(Math.log(sizeBytes.toDouble()) / Math.log(1024.0))).toInt()
    return "%.2f %s".format(sizeBytes / Math.pow(1024.0, i.toDouble()), units[i])
}

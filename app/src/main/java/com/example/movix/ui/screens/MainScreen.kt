package com.example.movix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.movix.data.local.WatchProgress
import com.example.movix.data.remote.model.TmdbMediaItem
import com.example.movix.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, viewModel: MediaViewModel = hiltViewModel()) {
    val trendingContent by viewModel.trendingContent.collectAsState()
    val featuredItem by viewModel.featuredItem.collectAsState()
    val continueWatching by viewModel.continueWatching.collectAsState()
    val playUrl by viewModel.playUrl.collectAsState()
    
    var isLoadingLink by remember { mutableStateOf(false) }
    var selectedProgress by remember { mutableStateOf<WatchProgress?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadTrending()
        viewModel.loadWatchProgress()
    }
    
    LaunchedEffect(playUrl) {
        playUrl?.let { url ->
            isLoadingLink = false
            selectedProgress?.let { progress ->
                navController.navigate(
                    Screen.Player.createRoute(
                        url = url,
                        title = progress.fileName ?: progress.title,
                        tmdbId = progress.id,
                        mediaType = progress.mediaType,
                        posterPath = progress.posterPath,
                        season = progress.season,
                        episode = progress.episode,
                        fileIdent = progress.fileIdent
                    )
                )
                viewModel.onPlayerNavigated()
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Movix") }) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                featuredItem?.let { item ->
                    item {
                        FeaturedComponent(item) {
                            navigateToMedia(navController, item)
                        }
                    }
                }

                if (continueWatching.isNotEmpty()) {
                    item {
                        ContinueWatchingRow(continueWatching) { progress ->
                            if (progress.fileIdent != null) {
                                selectedProgress = progress
                                isLoadingLink = true
                                viewModel.playStream(progress.fileIdent)
                            } else {
                                navController.navigate(
                                    Screen.Streams.createRoute(
                                        progress.id,
                                        progress.title.replace("+", " "),
                                        progress.mediaType,
                                        null,
                                        progress.season,
                                        progress.episode,
                                        progress.posterPath
                                    )
                                )
                            }
                        }
                    }
                }

                item {
                    ContentRow("Populární právě teď", trendingContent) { item ->
                        navigateToMedia(navController, item)
                    }
                }

                item {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Kategorie", style = MaterialTheme.typography.titleLarge)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CategoryCard("Filmy", Icons.Default.Movie, Modifier.weight(1f)) { 
                                navController.navigate(Screen.Genres.createRoute("movie")) 
                            }
                            CategoryCard("Seriály", Icons.Default.Tv, Modifier.weight(1f)) { 
                                navController.navigate(Screen.Genres.createRoute("tv")) 
                            }
                        }
                    }
                }
            }

            if (isLoadingLink) {
                AlertDialog(
                    onDismissRequest = { isLoadingLink = false },
                    title = { Text("Získávání odkazu") },
                    text = {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    },
                    confirmButton = {}
                )
            }
        }
    }
}

private fun navigateToMedia(navController: NavController, item: TmdbMediaItem) {
    if (item.mediaType == "movie") {
        navController.navigate(Screen.Streams.createRoute(
            item.id, item.title ?: "", "movie", item.releaseDate?.take(4), null, null, item.posterPath
        ))
    } else {
        navController.navigate(Screen.Seasons.createRoute(item.id, item.name ?: ""))
    }
}

@Composable
fun FeaturedComponent(item: TmdbMediaItem, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${item.posterPath}",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        startY = 300f
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = item.title ?: item.name ?: "",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onClick) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Přehrát")
            }
        }
    }
}

@Composable
fun ContinueWatchingRow(items: List<WatchProgress>, onItemClick: (WatchProgress) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Pokračovat ve sledování",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onItemClick(item) }
                ) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w300${item.posterPath}",
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Progress bar
                    val progress = if (item.duration > 0) item.position.toFloat() / item.duration else 0f
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .align(Alignment.BottomCenter),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.Gray.copy(alpha = 0.5f)
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                    startY = 50f
                                )
                            )
                    )
                    
                    Text(
                        text = item.title.replace("+", " "),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun ContentRow(title: String, items: List<TmdbMediaItem>, onItemClick: (TmdbMediaItem) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                Card(
                    modifier = Modifier
                        .width(120.dp)
                        .height(180.dp)
                        .clickable { onItemClick(item) }
                ) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w200${item.posterPath}",
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCard(title: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null)
            Text(title, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun MainMenuItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(16.dp))
        Text(title, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null)
    }
}

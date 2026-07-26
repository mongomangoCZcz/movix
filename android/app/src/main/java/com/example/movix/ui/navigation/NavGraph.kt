package com.example.movix.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.movix.ui.screens.*

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Search : Screen("search")
    object Discover : Screen("discover/{type}/{genreId}/{genreName}") {
        fun createRoute(type: String, genreId: Int, genreName: String) = "discover/$type/$genreId/${Uri.encode(genreName)}"
    }
    object Genres : Screen("genres/{type}") {
        fun createRoute(type: String) = "genres/$type"
    }
    object Alphabet : Screen("alphabet/{type}") {
        fun createRoute(type: String) = "alphabet/$type"
    }
    object Seasons : Screen("seasons/{tvId}/{title}") {
        fun createRoute(tvId: Int, title: String) = "seasons/$tvId/${Uri.encode(title)}"
    }
    object Episodes : Screen("episodes/{tvId}/{season}/{title}") {
        fun createRoute(tvId: Int, season: Int, title: String) = "episodes/$tvId/$season/${Uri.encode(title)}"
    }
    object Streams : Screen("streams/{tmdbId}/{title}/{mediaType}/{year}/{season}/{episode}/{posterPath}") {
        fun createRoute(tmdbId: Int, title: String, mediaType: String, year: String? = null, season: Int? = null, episode: Int? = null, posterPath: String? = null) = 
            "streams/$tmdbId/${Uri.encode(title)}/$mediaType/${year ?: "null"}/${season ?: -1}/${episode ?: -1}/${if (posterPath != null) Uri.encode(posterPath) else "null"}"
    }
    object Player : Screen("player/{url}/{title}/{tmdbId}/{mediaType}/{posterPath}/{season}/{episode}/{fileIdent}") {
        fun createRoute(
            url: String, 
            title: String, 
            tmdbId: Int = 0, 
            mediaType: String = "local", 
            posterPath: String? = null, 
            season: Int? = null, 
            episode: Int? = null,
            fileIdent: String? = null
        ) = "player/${Uri.encode(url)}/${Uri.encode(title)}/$tmdbId/$mediaType/${if (posterPath != null) Uri.encode(posterPath) else "null"}/${season ?: -1}/${episode ?: -1}/${fileIdent ?: "null"}"
    }
    object Downloads : Screen("downloads")
    object Settings : Screen("settings")
    object Profile : Screen("profile")
}

@Composable
fun MovixNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) { MainScreen(navController) }
        composable(Screen.Search.route) { SearchScreen(navController) }
        composable(Screen.Genres.route) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "movie"
            GenresScreen(navController, type)
        }
        composable(Screen.Alphabet.route) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "movie"
            AlphabetScreen(navController, type)
        }
        composable(Screen.Discover.route) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "movie"
            val genreId = backStackEntry.arguments?.getString("genreId")?.toIntOrNull()
            val genreName = backStackEntry.arguments?.getString("genreName") ?: ""
            DiscoverScreen(navController, type, genreId, genreName)
        }
        composable(Screen.Seasons.route) { backStackEntry ->
            val tvId = backStackEntry.arguments?.getString("tvId")?.toIntOrNull() ?: 0
            val title = backStackEntry.arguments?.getString("title") ?: ""
            SeasonsScreen(navController, tvId, title)
        }
        composable(Screen.Episodes.route) { backStackEntry ->
            val tvId = backStackEntry.arguments?.getString("tvId")?.toIntOrNull() ?: 0
            val season = backStackEntry.arguments?.getString("season")?.toIntOrNull() ?: 1
            val title = backStackEntry.arguments?.getString("title") ?: ""
            EpisodesScreen(navController, tvId, season, title)
        }
        composable(Screen.Streams.route) { backStackEntry ->
            val tmdbId = backStackEntry.arguments?.getString("tmdbId")?.toIntOrNull() ?: 0
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val mediaType = backStackEntry.arguments?.getString("mediaType") ?: "movie"
            val year = backStackEntry.arguments?.getString("year").let { if (it == "null") null else it }
            val season = backStackEntry.arguments?.getString("season")?.toIntOrNull().let { if (it == -1) null else it }
            val episode = backStackEntry.arguments?.getString("episode")?.toIntOrNull().let { if (it == -1) null else it }
            val posterPath = backStackEntry.arguments?.getString("posterPath").let { if (it == "null") null else it }
            StreamsScreen(navController, tmdbId, title, mediaType, year, season, episode, posterPath)
        }
        composable(Screen.Player.route) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val tmdbId = backStackEntry.arguments?.getString("tmdbId")?.toIntOrNull() ?: 0
            val mediaType = backStackEntry.arguments?.getString("mediaType") ?: "movie"
            val posterPath = backStackEntry.arguments?.getString("posterPath").let { if (it == "null") null else it }
            val season = backStackEntry.arguments?.getString("season")?.toIntOrNull().let { if (it == -1) null else it }
            val episode = backStackEntry.arguments?.getString("episode")?.toIntOrNull().let { if (it == -1) null else it }
            val fileIdent = backStackEntry.arguments?.getString("fileIdent").let { if (it == "null") null else it }
            
            PlayerScreen(navController, url, title, tmdbId, mediaType, posterPath, season, episode, fileIdent)
        }
        composable(Screen.Downloads.route) { DownloadsScreen(navController) }
        composable(Screen.Settings.route) { SettingsScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController) }
    }
}

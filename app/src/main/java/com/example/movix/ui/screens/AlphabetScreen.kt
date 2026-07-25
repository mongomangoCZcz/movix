package com.example.movix.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movix.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlphabetScreen(navController: NavController, mediaType: String) {
    val letters = ('A'..'Z').toList() + listOf("0-9")

    Scaffold(topBar = { TopAppBar(title = { Text("Abeceda: ${if (mediaType == "movie") "Filmy" else "Seriály"}") }) }) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(letters) { letter ->
                Card(Modifier.fillMaxWidth().clickable {
                    // TMDb doesn't have a direct "starts with" filter easily for discover without searching
                    // But we can navigate to search with the letter as query
                    navController.navigate(Screen.Search.route) // Simple fallback for now
                }) {
                    Text(letter.toString(), modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

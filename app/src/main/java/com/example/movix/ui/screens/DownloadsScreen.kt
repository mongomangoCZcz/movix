package com.example.movix.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.movix.data.repository.PreferencesRepository
import com.example.movix.ui.navigation.Screen
import kotlinx.coroutines.flow.first
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(navController: NavController, viewModel: DownloadsViewModel = hiltViewModel()) {
    val files by viewModel.files.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadFiles()
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Stažené soubory") }) }) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(files) { file ->
                Card(Modifier.fillMaxWidth().clickable {
                    navController.navigate(Screen.Player.createRoute(file.absolutePath, file.name))
                }) {
                    Text(file.name, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

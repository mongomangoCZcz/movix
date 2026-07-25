package com.example.movix.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movix.data.repository.AuthRepository
import com.example.movix.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var status by mutableStateOf("")

    init {
        viewModelScope.launch {
            username = preferencesRepository.username.first() ?: ""
        }
    }

    fun login() {
        viewModelScope.launch {
            status = "Přihlašování..."
            val result = authRepository.login(username, password)
            result.onSuccess {
                preferencesRepository.saveWstToken(it)
                preferencesRepository.saveUsername(username)
                status = "Přihlášení úspěšné"
            }.onFailure {
                status = "Chyba: ${it.message}"
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: androidx.navigation.NavController, viewModel: SettingsViewModel = hiltViewModel()) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Nastavení") }) }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = viewModel.username,
                onValueChange = { viewModel.username = it },
                label = { Text("Webshare uživatelské jméno") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                label = { Text("Webshare heslo") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { viewModel.login() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Přihlásit se")
            }
            Text(viewModel.status)
        }
    }
}

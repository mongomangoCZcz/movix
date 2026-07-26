package com.example.movix.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movix.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DownloadsViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _files = MutableStateFlow<List<File>>(emptyList())
    val files: StateFlow<List<File>> = _files

    fun loadFiles() {
        viewModelScope.launch {
            val path = preferencesRepository.downloadFolder.first() ?: appContext.filesDir.absolutePath
            val folder = File(path)
            _files.value = folder.listFiles { _, name -> 
                name.endsWith(".mp4") || name.endsWith(".mkv") || name.endsWith(".avi") 
            }?.toList() ?: emptyList()
        }
    }
}

package com.example.movix.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class PreferencesRepository @Inject constructor(
    @ApplicationContext private val appContext: Context
) {
    companion object {
        val WST_TOKEN = stringPreferencesKey("wst_token")
        val USERNAME = stringPreferencesKey("username")
        val DOWNLOAD_FOLDER = stringPreferencesKey("download_folder")
    }

    val wstToken: Flow<String?> = appContext.dataStore.data.map { it[WST_TOKEN] }
    val username: Flow<String?> = appContext.dataStore.data.map { it[USERNAME] }
    val downloadFolder: Flow<String?> = appContext.dataStore.data.map { it[DOWNLOAD_FOLDER] }

    suspend fun saveWstToken(token: String) {
        appContext.dataStore.edit { it[WST_TOKEN] = token }
    }

    suspend fun saveUsername(username: String) {
        appContext.dataStore.edit { it[USERNAME] = username }
    }

    suspend fun saveDownloadFolder(path: String) {
        appContext.dataStore.edit { it[DOWNLOAD_FOLDER] = path }
    }
}

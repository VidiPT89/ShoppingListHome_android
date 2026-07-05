package com.shoppinglisthome.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

enum class ThemePreference { SYSTEM, LIGHT, DARK }

class SettingsRepository(private val context: Context) {

    private object Keys {
        val THEME = intPreferencesKey("theme_preference")
        val LANGUAGE = stringPreferencesKey("language_preference")
    }

    val theme: Flow<ThemePreference> = context.dataStore.data.map { prefs ->
        when (prefs[Keys.THEME] ?: 0) {
            1 -> ThemePreference.LIGHT
            2 -> ThemePreference.DARK
            else -> ThemePreference.SYSTEM
        }
    }

    val language: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.LANGUAGE] ?: "pt"
    }

    suspend fun setTheme(theme: ThemePreference) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME] = when (theme) {
                ThemePreference.SYSTEM -> 0
                ThemePreference.LIGHT -> 1
                ThemePreference.DARK -> 2
            }
        }
    }

    suspend fun setLanguage(code: String) {
        context.dataStore.edit { prefs -> prefs[Keys.LANGUAGE] = code }
    }
}

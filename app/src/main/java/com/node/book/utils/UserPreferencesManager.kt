package com.node.book.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// ─── Creates the DataStore instance ───────────────────────
private val Context.dataStore: DataStore<Preferences>
        by preferencesDataStore(name = "user_preferences")

// ─── UserPreferencesManager ────────────────────────────
@Singleton
class UserPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // ─── Keys (like column names in a table) ──────────────
    companion object {
        val OWNER_NAME = stringPreferencesKey("owner_name")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val USE_SYSTEM_THEME = booleanPreferencesKey("use_system_theme")
    }

    // ─── Read owner name ──────────────────────────────────
    val ownerName: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[OWNER_NAME] ?: "My Notes" }

    // ─── Read dark mode ───────────────────────────────────
    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[IS_DARK_MODE] ?: false }

    // ─── Read system theme preference ────────────────────
    val useSystemTheme: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[USE_SYSTEM_THEME] ?: true }

    // ─── Write owner name ─────────────────────────────────
    suspend fun setOwnerName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[OWNER_NAME] = name
        }
    }

    // ─── Write dark mode ──────────────────────────────────
    suspend fun setDarkMode(isDark: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_DARK_MODE] = isDark
        }
    }

    // ─── Write system theme preference ───────────────────
    suspend fun setUseSystemTheme(useSystem: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[USE_SYSTEM_THEME] = useSystem
        }
    }
}
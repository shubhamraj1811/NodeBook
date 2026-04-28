package com.node.book.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.node.book.utils.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
// ─── SettingsViewModel ────────────────────────────────
class SettingsViewModel @Inject constructor(
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    // ─── Expose settings as StateFlow ─────────────────────
    val ownerName: StateFlow<String> = preferencesManager.ownerName
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "My Notes"
        )

    val isDarkMode: StateFlow<Boolean> = preferencesManager.isDarkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    // ───
    val useSystemTheme: StateFlow<Boolean> = preferencesManager.useSystemTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    // ─── Actions ─────────────────────────────────────────
    fun updateOwnerName(name: String) {
        viewModelScope.launch {
            preferencesManager.setOwnerName(name)
        }
    }

    // ─── Actions ─────────────────────────────────────────
    fun toggleDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            preferencesManager.setDarkMode(isDark)
        }
    }

    // ───
    fun toggleUseSystemTheme(useSystem: Boolean) {
        viewModelScope.launch {
            preferencesManager.setUseSystemTheme(useSystem)
        }
    }
}
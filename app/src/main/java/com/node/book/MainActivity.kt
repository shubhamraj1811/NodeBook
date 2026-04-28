package com.node.book

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.node.book.ui.NotesNavHost
import com.node.book.ui.theme.NotesAppTheme
import com.node.book.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // ─── Get settings to drive theme ──────────────
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
            val useSystemTheme by settingsViewModel.useSystemTheme.collectAsState()
            val systemDark = isSystemInDarkTheme()

            // ─── Decide which theme to use ────────────────
            val darkTheme = if (useSystemTheme) systemDark else isDarkMode

            NotesAppTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()
                NotesNavHost(navController = navController)
            }
        }
    }
}
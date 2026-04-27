package com.node.book.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.node.book.utils.Screen
import com.node.book.ui.screens.HomeScreen
import com.node.book.ui.screens.NoteEditorScreen

@Composable
fun NotesNavHost(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route    // app always opens here
    ) {

        // ─── Home Screen ──────────────────────────────────
        composable(route = Screen.Home.route) {
            HomeScreen(
                onNoteClick = { noteId ->
                    navController.navigate(Screen.NoteEditor.createRoute(noteId))
                },
                onCreateNote = {
                    navController.navigate(Screen.NoteEditor.createRoute())
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        // ─── Note Editor Screen ───────────────────────────
        composable(
            route = Screen.NoteEditor.route,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
            NoteEditorScreen(
                noteId = noteId,
                onBack = { navController.popBackStack() }
            )
        }

        // ─── Folder Detail Screen ─────────────────────────
        composable(
            route = Screen.FolderDetail.route,
            arguments = listOf(
                navArgument("folderId") {
                    type = NavType.IntType
                },
                navArgument("folderName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val folderId = backStackEntry.arguments?.getInt("folderId") ?: 0
            val folderName = backStackEntry.arguments?.getString("folderName") ?: ""
            // FolderDetailScreen(folderId, folderName) ← plugged in later
        }

        // ─── Settings Screen ──────────────────────────────
        composable(route = Screen.Settings.route) {
            // SettingsScreen() ← plugged in later
        }
    }
}
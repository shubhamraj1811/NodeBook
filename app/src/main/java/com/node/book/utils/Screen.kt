package com.node.book.utils

sealed class Screen(val route: String) {
    // ─── Main Screens ─────────────────────────────────────
    object Home : Screen("home")
    object Settings : Screen("settings")
    // ─── Note Editor (needs a noteId) ─────────────────────
    // noteId = -1 means "creating a new note"
    object NoteEditor : Screen("note_editor/{noteId}") {
        fun createRoute(noteId: Int = -1) = "note_editor/$noteId"
    }
    // ─── Folder Contents (needs a folderId + folder name) ──
    object FolderDetail : Screen("folder/{folderId}/{folderName}") {
        fun createRoute(folderId: Int, folderName: String) =
            "folder/$folderId/$folderName"
    }
}
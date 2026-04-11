package com.node.book.data.local

// --- Imports -----------------
import androidx.room.Database
import androidx.room.RoomDatabase
import com.node.book.data.model.Folder
import com.node.book.data.model.Note

// --- Database class ----------
@Database(
    entities = [Note::class, Folder::class],
    version = 1,
    exportSchema = false
)

// --- Abstract class ----------
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun folderDao(): FolderDao

    // --- Companion object ----------
    companion object {
        const val DATABASE_NAME = "notes_database"
    }
}
package com.node.book.data.local
// SQL queries for notes

// --- Imports -----------------
import androidx.room.*
import com.node.book.data.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    // --- Query For All Notes -----------------
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<Note>>

    // --- Query For Notes By Folder -----------------
    @Query("SELECT * FROM notes WHERE folderId = :folderId ORDER BY updatedAt DESC")
    fun getNotesByFolder(folderId: Int): Flow<List<Note>>

    // --- Query For Unorganized Notes -----------------
    @Query("SELECT * FROM notes WHERE folderId IS NULL ORDER BY updatedAt DESC")
    fun getUnorganizedNotes(): Flow<List<Note>>

    // --- Query For Note By Id -----------------
    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(Id: Int): Note?

    // --- Query For Search -----------------
    @Query("""
        SELECT * FROM notes
        WHERE title LIKE '%' || :query || '%'
        OR content LIKE '%' || :query || '%'
        ORDER BY updatedAt DESC
    """)

    // --- Query For Search -----------------
    fun searchNotes(query: String): Flow<List<Note>>

    // --- Query For Update -----------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    // --- Query For Update -----------------
    @Update
    suspend fun updateNote(note: Note)

    // --- Query For Delete -----------------
    @Delete
    suspend fun deleteNote(note: Note)
}
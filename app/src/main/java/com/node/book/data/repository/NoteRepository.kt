package com.node.book.data.repository

// --- imports ----------
import com.node.book.data.local.FolderDao
import com.node.book.data.local.NoteDao
import com.node.book.data.model.Folder
import com.node.book.data.model.Note
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val folderDao: FolderDao
) {
    // --- Notes ----------
    fun getAllNotes(): Flow<List<Note>> =
        noteDao.getAllNotes()

    fun getNotesByFolder(folderId: Int): Flow<List<Note>> =
        noteDao.getNotesByFolder(folderId)

    fun getUnorganizedNotes(): Flow<List<Note>> =
        noteDao.getUnorganizedNotes()

    suspend fun getNoteById(id: Int): Note? =
        noteDao.getNoteById(id)

    fun searchNotes(query: String): Flow<List<Note>> =
        noteDao.searchNotes(query)

    suspend fun insertNote(note: Note): Long =
        noteDao.insertNote(note)

    suspend fun updateNote(note: Note) =
        noteDao.updateNote(note)

    suspend fun deleteNote(note: Note) =
        noteDao.deleteNote(note)

    // --- All Folders ----------
    fun getAllFolders(): Flow<List<Folder>> =
        folderDao.getAllFolders()

    // --- Folder By Id ----------
    suspend fun getFolderById(id: Int): Folder? =
        folderDao.getFolderById(id)

    // --- Insert Folder ----------
    suspend fun insertFolder(folder: Folder): Long =
        folderDao.insertFolder(folder)

    // --- Update Folder ----------
    suspend fun updateFolder(folder: Folder) =
        folderDao.updateFolder(folder)

    // --- Delete Folder ----------
    suspend fun deleteFolder(folder: Folder) =
        folderDao.deleteFolder(folder)
}
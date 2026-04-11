package com.node.book.data.local
// --- SQL Queries for Folder --------------

// --- Imports -----------------
import androidx.room.*
import com.node.book.data.model.Folder
import kotlinx.coroutines.flow.Flow

// --- FolderDao -----------------
@Dao
interface FolderDao {
    // --- Query For All Folders -----------------
    @Query("SELECT * FROM folders ORDER BY createdAt DESC")
    fun getAllFolders(): Flow<List<Folder>>

    // --- Query For Folder By Id -----------------
    @Query("SELECT * FROM folders WHERE id = :id")
    suspend fun getFolderById(id: Int): Folder?

    // --- Query For Search -----------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: Folder): Long

    // --- Query For Update -----------------
    @Update
    suspend fun updateFolder(folder: Folder)

    // --- Query For Delete -----------------
    @Delete
    suspend fun deleteFolder(folder: Folder)
}
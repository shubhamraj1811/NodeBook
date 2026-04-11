package com.node.book.data.model

// --- Imports -----------------
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// --- Folder Entity -----------------
@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = Folder::class,
            parentColumns = ["id"],
            childColumns = ["folderId"],
            onDelete = ForeignKey.SET_NULL   // if folder deleted, note stays
        )
    ]
)
// --- Note Entity -----------------
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val content: String,

    val folderId: Int? = null,           // null = note is not in any folder

    val backgroundColor: String = "#FFFFFF",
    val backgroundWallpaper: String? = null,
    val textSize: Float = 16f,

    val isBold: Boolean = false,
    val isItalic: Boolean = false,

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
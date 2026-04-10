package com.node.book.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity ("folders" )
data class `Folder.kt`(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)
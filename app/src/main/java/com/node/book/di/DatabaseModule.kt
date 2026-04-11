package com.node.book.di

// --- work of this file :
import android.content.Context
import androidx.room.Room
import com.node.book.data.local.FolderDao
import com.node.book.data.local.NoteDao
import com.node.book.data.local.NotesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// --- DatabaseModule -----------------
@Module

// --- InstallIn -----------------
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // --- Provides Database -----------------
    @Provides
    @Singleton

    // --- fun use : return database -----------------
    fun provideDatabase(
        @ApplicationContext context: Context
    ) : NotesDatabase { // --- work : return database -----------------
        return Room.databaseBuilder(
            context,
            NotesDatabase::class.java,
            NotesDatabase.DATABASE_NAME
        ).build()
    }

    // --- Provides NoteDao -----------------
    @Provides
    @Singleton
    fun provideNotesDao(database: NotesDatabase): NoteDao =
        database.noteDao()

    // --- Provides FolderDao -----------------
    @Provides
    @Singleton
    fun provideFolderDao(database: NotesDatabase): FolderDao =
        database.folderDao()
}
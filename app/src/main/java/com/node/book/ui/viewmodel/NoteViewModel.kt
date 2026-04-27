package com.node.book.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.node.book.data.model.Note
import com.node.book.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    // ─── Search ──────────────────────────────────────────
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // ─── Current Folder Filter ───────────────────────────
    private val _selectedFolderId = MutableStateFlow<Int?>(null)
    val selectedFolderId: StateFlow<Int?> = _selectedFolderId.asStateFlow()

    // ─── Notes List (reacts to search + folder filter) ───
    val notes: StateFlow<List<Note>> = combine(
        _searchQuery,
        _selectedFolderId
    ) { query, folderId ->
        Pair(query, folderId)
    }.flatMapLatest { (query, folderId) ->
        when {
            query.isNotBlank() -> repository.searchNotes(query)
            folderId != null   -> repository.getNotesByFolder(folderId)
            else               -> repository.getAllNotes()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // ─── Currently Opened Note ───────────────────────────
    private val _selectedNote = MutableStateFlow<Note?>(null)
    val selectedNote: StateFlow<Note?> = _selectedNote.asStateFlow()

    // ─── Actions ─────────────────────────────────────────
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFolderSelected(folderId: Int?) {
        _selectedFolderId.value = folderId
    }

    fun selectNote(note: Note?) {
        _selectedNote.value = note
    }

    // ─── Get Note By Id ──────────────────────────────────  ← NEW
    suspend fun getNoteById(id: Int): Note? =
        repository.getNoteById(id)


    // ─── Save Note ───────────────────────────────────────
    fun saveNote(note: Note) {
        viewModelScope.launch {
            if (note.id == 0) {
                repository.insertNote(note)
            } else {
                repository.updateNote(note.copy(updatedAt = System.currentTimeMillis()))
            }
        }
    }

    // ─── Delete Note ─────────────────────────────────────
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
}
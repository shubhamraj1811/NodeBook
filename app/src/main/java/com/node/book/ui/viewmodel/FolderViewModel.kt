package com.node.book.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.node.book.data.model.Folder
import com.node.book.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {
    // ─── All Folders (live) ───────────────────────────────
    val folders: StateFlow<List<Folder>> = repository.getAllFolders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    // ─── Actions ─────────────────────────────────────────
    fun createFolder(name: String) {
        viewModelScope.launch {
            repository.insertFolder(Folder(name = name))
        }
    }
    // ─── Rename Folder ───────────────────────────────────
    fun renameFolder(folder: Folder, newName: String) {
        viewModelScope.launch {
            repository.updateFolder(folder.copy(name = newName))
        }
    }
    // ─── Delete Folder ───────────────────────────────────
    fun deleteFolder(folder: Folder) {
        viewModelScope.launch {
            repository.deleteFolder(folder)
        }
    }
}
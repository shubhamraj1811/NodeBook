package com.node.book.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.node.book.ui.components.NoteCard
import com.node.book.ui.viewmodel.FolderViewModel
import com.node.book.ui.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderDetailScreen(
    folderId: Int,
    folderName: String,
    onNoteClick: (Int) -> Unit,
    onCreateNote: () -> Unit,
    onBack: () -> Unit
) {
    val noteViewModel: NoteViewModel = hiltViewModel()
    val folderViewModel: FolderViewModel = hiltViewModel()

    // ─── Load notes for this folder ───────────────────────
    LaunchedEffect(folderId) {
        noteViewModel.onFolderSelected(folderId)
    }

    val notes by noteViewModel.notes.collectAsState()

    // ─── Rename + Delete dialog state ────────────────────
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var renameInput by remember { mutableStateOf(folderName) }

    Scaffold(
        // ─── Top Bar ──────────────────────────────────────
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = folderName,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${notes.size} notes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        noteViewModel.onFolderSelected(null)
                        onBack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Rename folder
                    IconButton(onClick = { showRenameDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Rename Folder"
                        )
                    }
                    // Delete folder
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Folder",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },

        // ─── FAB ──────────────────────────────────────────
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateNote,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Note",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

    ) { paddingValues ->

        if (notes.isEmpty()) {
            // ─── Empty State ───────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                            .copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Folder is empty",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                            .copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap + to add a note here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                            .copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            // ─── Notes List ───────────────────────────────
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(
                    items = notes,
                    key = { it.id }
                ) { note ->
                    NoteCard(
                        note = note,
                        onClick = { onNoteClick(note.id) },
                        onDelete = { noteViewModel.deleteNote(note) }
                    )
                }
            }
        }
    }

    // ─── Rename Dialog ────────────────────────────────────
    if (showRenameDialog) {
        val folders by folderViewModel.folders.collectAsState()
        val currentFolder = folders.find { it.id == folderId }

        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename Folder") },
            text = {
                OutlinedTextField(
                    value = renameInput,
                    onValueChange = { renameInput = it },
                    placeholder = { Text("Folder name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (renameInput.isNotBlank() && currentFolder != null) {
                            folderViewModel.renameFolder(
                                currentFolder,
                                renameInput.trim()
                            )
                            showRenameDialog = false
                        }
                    }
                ) { Text("Rename") }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // ─── Delete Folder Dialog ─────────────────────────────
    if (showDeleteDialog) {
        val folders by folderViewModel.folders.collectAsState()
        val currentFolder = folders.find { it.id == folderId }

        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Folder?") },
            text = {
                Text(
                    "The folder will be deleted but all notes inside " +
                            "will be kept in All Notes."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        currentFolder?.let {
                            folderViewModel.deleteFolder(it)
                        }
                        showDeleteDialog = false
                        noteViewModel.onFolderSelected(null)
                        onBack()
                    }
                ) {
                    Text(
                        "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
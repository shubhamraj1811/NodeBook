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
import com.node.book.data.model.Folder
import com.node.book.ui.components.DrawerContent
import com.node.book.ui.components.NoteCard
import com.node.book.ui.viewmodel.FolderViewModel
import com.node.book.ui.viewmodel.NoteViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNoteClick: (Int) -> Unit,
    onCreateNote: () -> Unit,
    onSettingsClick: () -> Unit
) {
    // ─── ViewModels (Hilt gives these automatically) ──────
    val noteViewModel: NoteViewModel = hiltViewModel()
    val folderViewModel: FolderViewModel = hiltViewModel()

    // ─── Collect State ────────────────────────────────────
    val notes by noteViewModel.notes.collectAsState()
    val folders by folderViewModel.folders.collectAsState()
    val searchQuery by noteViewModel.searchQuery.collectAsState()
    val selectedFolderId by noteViewModel.selectedFolderId.collectAsState()

    // ─── Drawer State ─────────────────────────────────────
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // ─── Dialog State (create folder) ────────────────────
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var folderNameInput by remember { mutableStateOf("") }

    // ─── Current folder name for TopBar title ─────────────
    val currentFolderName = remember(selectedFolderId, folders) {
        folders.find { it.id == selectedFolderId }?.name ?: "All Notes"
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                folders = folders,
                selectedFolderId = selectedFolderId,
                onAllNotesClick = {
                    noteViewModel.onFolderSelected(null)
                    scope.launch { drawerState.close() }
                },
                onFolderClick = { folder ->
                    noteViewModel.onFolderSelected(folder.id)
                    scope.launch { drawerState.close() }
                },
                onSettingsClick = {
                    scope.launch { drawerState.close() }
                    onSettingsClick()
                },
                onCreateFolderClick = {
                    showCreateFolderDialog = true
                }
            )
        }
    ) {
        Scaffold(
            // ─── Top App Bar ──────────────────────────────
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 48.dp, bottom = 8.dp)
                ) {
                    // Title row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Hamburger menu
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = currentFolderName,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        // Note count badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${notes.size}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 4.dp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // ─── Search Bar ───────────────────────
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { noteViewModel.onSearchQueryChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Search notes...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = {
                                    noteViewModel.onSearchQueryChange("")
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear"
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            },

            // ─── FAB (Create Note) ────────────────────────
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

            // ─── Notes List ───────────────────────────────
            if (notes.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.NoteAdd,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No notes yet",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap + to create your first note",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    }
                }
            } else {
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
                        key = { it.id }      // stable key = smooth animations
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
    }

    // ─── Create Folder Dialog ─────────────────────────────
    if (showCreateFolderDialog) {
        AlertDialog(
            onDismissRequest = {
                showCreateFolderDialog = false
                folderNameInput = ""
            },
            title = { Text("New Folder") },
            text = {
                OutlinedTextField(
                    value = folderNameInput,
                    onValueChange = { folderNameInput = it },
                    placeholder = { Text("Folder name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (folderNameInput.isNotBlank()) {
                            folderViewModel.createFolder(folderNameInput.trim())
                            showCreateFolderDialog = false
                            folderNameInput = ""
                        }
                    }
                ) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCreateFolderDialog = false
                    folderNameInput = ""
                }) { Text("Cancel") }
            }
        )
    }
}
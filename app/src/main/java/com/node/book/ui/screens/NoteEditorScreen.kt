package com.node.book.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.node.book.data.model.Note
import com.node.book.ui.components.FormattingToolbar
import com.node.book.ui.viewmodel.FolderViewModel
import com.node.book.ui.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    noteId: Int,
    onBack: () -> Unit
) {
    val noteViewModel: NoteViewModel = hiltViewModel()
    val folderViewModel: FolderViewModel = hiltViewModel()
    val folders by folderViewModel.folders.collectAsState()

    // ─── Local editor state ───────────────────────────────
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var backgroundColor by remember { mutableStateOf("#FFFFFF") }
    var textSize by remember { mutableFloatStateOf(16f) }
    var isBold by remember { mutableStateOf(false) }
    var isItalic by remember { mutableStateOf(false) }
    var selectedFolderId by remember { mutableStateOf<Int?>(null) }

    // ─── Toolbar + Folder sheet visibility ───────────────
    var showToolbar by remember { mutableStateOf(false) }
    var showFolderSheet by remember { mutableStateOf(false) }

    // ─── Load existing note if editing ───────────────────
    var existingNote by remember { mutableStateOf<Note?>(null) }

    LaunchedEffect(noteId) {
        if (noteId != -1) {
            noteViewModel.getNoteById(noteId)?.let { note ->  // ← fix: instance not class
                existingNote = note
                title = note.title
                content = note.content
                backgroundColor = note.backgroundColor
                textSize = note.textSize
                isBold = note.isBold
                isItalic = note.isItalic
                selectedFolderId = note.folderId
            }
        }
    }

    // ─── Save note function ───────────────────────────────
    fun saveNote() {
        if (title.isBlank() && content.isBlank()) {
            onBack()
            return
        }
        val note = Note(
            id = existingNote?.id ?: 0,
            title = title.trim(),
            content = content.trim(),
            backgroundColor = backgroundColor,
            textSize = textSize,
            isBold = isBold,
            isItalic = isItalic,
            folderId = selectedFolderId,
            createdAt = existingNote?.createdAt ?: System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        noteViewModel.saveNote(note)
        onBack()
    }

    // ─── Background color of the editor ──────────────────
    val bgColor = try {
        Color(android.graphics.Color.parseColor(backgroundColor))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.surface
    }

    // ─── Folder bottom sheet ──────────────────────────────
    if (showFolderSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFolderSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Move to Folder",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                ListItem(
                    headlineContent = { Text("No Folder") },
                    leadingContent = {
                        Icon(Icons.Default.Notes, contentDescription = null)
                    },
                    trailingContent = {
                        if (selectedFolderId == null) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    modifier = Modifier.clickableWithRipple {
                        selectedFolderId = null
                        showFolderSheet = false
                    }
                )

                HorizontalDivider()

                folders.forEach { folder ->
                    ListItem(
                        headlineContent = { Text(folder.name) },
                        leadingContent = {
                            Icon(Icons.Default.Folder, contentDescription = null)
                        },
                        trailingContent = {
                            if (selectedFolderId == folder.id) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        modifier = Modifier.clickableWithRipple {
                            selectedFolderId = folder.id
                            showFolderSheet = false
                        }
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // ─── Main Editor Layout ───────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {

        // ─── Top Bar ──────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { saveNote() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { showFolderSheet = true }) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = "Folder",
                    tint = if (selectedFolderId != null)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { showToolbar = !showToolbar }) {
                Icon(
                    imageVector = Icons.Default.TextFormat,
                    contentDescription = "Format",
                    tint = if (showToolbar)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { saveNote() }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // ─── Formatting Toolbar ───────────────────────────
        if (showToolbar) {
            FormattingToolbar(
                isBold = isBold,
                isItalic = isItalic,
                textSize = textSize,
                selectedColor = backgroundColor,
                onBoldToggle = { isBold = !isBold },
                onItalicToggle = { isItalic = !isItalic },
                onTextSizeIncrease = { if (textSize < 32f) textSize += 2f },
                onTextSizeDecrease = { if (textSize > 10f) textSize -= 2f },
                onColorSelected = { backgroundColor = it }
            )
        }

        // ─── Scrollable Writing Area ──────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            BasicTextField(
                value = title,
                onValueChange = { title = it },
                textStyle = TextStyle(
                    fontSize = (textSize + 6).sp,
                    fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
                    fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                decorationBox = { innerTextField ->
                    if (title.isEmpty()) {
                        Text(
                            text = "Title",
                            style = TextStyle(
                                fontSize = (textSize + 6).sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    }
                    innerTextField()
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formatDate(System.currentTimeMillis()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(16.dp))

            BasicTextField(
                value = content,
                onValueChange = { content = it },
                textStyle = TextStyle(
                    fontSize = textSize.sp,
                    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                    fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                decorationBox = { innerTextField ->
                    if (content.isEmpty()) {
                        Text(
                            text = "Start writing...",
                            style = TextStyle(
                                fontSize = textSize.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    }
                    innerTextField()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 300.dp)
            )
        }
    }
}

// ─── Date formatter ──────────────────────────────────────  ← NEW
private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

// ─── Clickable with ripple ────────────────────────────────
private fun Modifier.clickableWithRipple(onClick: () -> Unit): Modifier =
    this.clickable(onClick = onClick)
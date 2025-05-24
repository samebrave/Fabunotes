package com.samprojects.fabunotes.notes

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.samprojects.fabunotes.R
import com.samprojects.fabunotes.data.Note
import com.samprojects.fabunotes.model.NoteViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteList(
    notes: List<Note>,
    viewModel: NoteViewModel,
    navController: NavController,
    paddingValues: PaddingValues,
    selectedNote: Note?,
    onNoteSelected: (Note?) -> Unit
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = selectedNote != null) {
        onNoteSelected(null)
    }

    val sortedNotes = remember(notes) {
        notes.sortedWith(compareByDescending<Note> { it.isPinned }.thenByDescending { it.id })
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(bottom = if (selectedNote != null) 80.dp else 0.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = sortedNotes,
                key = { it.id }
            ) { note ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    NoteItem(
                        note = note,
                        viewModel = viewModel,
                        navController = navController,
                        isGridItem = false,
                        isSelected = selectedNote == note,
                        onLongPress = { onNoteSelected(it) }
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = selectedNote != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BottomAppBar {
                IconButton(
                    onClick = {
                        selectedNote?.let { viewModel.togglePinned(it) }
                        onNoteSelected(null)
                    }
                ) {
                    Icon(
                        imageVector = if (selectedNote?.isPinned == true) Icons.Default.PushPin else Icons.Outlined.PushPin,
                        contentDescription = stringResource(R.string.toggle_pin)
                    )
                }
                IconButton(
                    onClick = { showDeleteConfirmDialog = true }
                ) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete_note))
                }
                IconButton(
                    onClick = {
                        val noteText = "${selectedNote?.title}\n\n${selectedNote?.content}"
                        clipboardManager.setText(AnnotatedString(noteText))
                        Toast.makeText(context, "Note copied to clipboard", Toast.LENGTH_SHORT).show()
                        onNoteSelected(null)
                    }
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy Note")
                }
                IconButton(
                    onClick = { onNoteSelected(null) }
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text(stringResource(R.string.delete_note)) },
            text = { Text(stringResource(R.string.delete_note_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedNote?.let { viewModel.deleteNote(it) }
                        showDeleteConfirmDialog = false
                        onNoteSelected(null)
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
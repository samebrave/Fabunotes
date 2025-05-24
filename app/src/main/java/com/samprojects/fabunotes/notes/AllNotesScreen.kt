package com.samprojects.fabunotes.notes

import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.samprojects.fabunotes.R
import com.samprojects.fabunotes.data.Note
import com.samprojects.fabunotes.model.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllNotesScreen(viewModel: NoteViewModel, navController: androidx.navigation.NavController) {
    val notes by viewModel.allNotes.collectAsState(initial = emptyList())
    var selectedNote by remember { mutableStateOf<Note?>(null) }

    BackHandler {
        if (selectedNote != null) {
            selectedNote = null
        } else {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.all_notes)) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedNote != null) {
                            selectedNote = null
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleNoteViewMode() }) {
                        Icon(
                            imageVector = if (viewModel.isNoteGridView) Icons.Default.List else Icons.Default.GridView,
                            contentDescription = stringResource(R.string.toggle_view_mode)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (viewModel.isNoteGridView) {
            NoteGrid(
                notes = notes,
                viewModel = viewModel,
                navController = navController,
                paddingValues = paddingValues,
                selectedNote = selectedNote,
                onNoteSelected = { selectedNote = it }
            )
        } else {
            NoteList(
                notes = notes,
                viewModel = viewModel,
                navController = navController,
                paddingValues = paddingValues,
                selectedNote = selectedNote,
                onNoteSelected = { selectedNote = it }
            )
        }
    }
}
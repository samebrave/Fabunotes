package com.samprojects.fabunotes.category

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.samprojects.fabunotes.R
import com.samprojects.fabunotes.model.NoteViewModel

@Composable
fun AddCategoryDialog(viewModel: NoteViewModel, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.add_category)) },
        text = {
            TextField(
                value = viewModel.currentCategoryName,
                onValueChange = { viewModel.updateCategoryName(it) },
                label = { Text(stringResource(id = R.string.category_name)) }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.addCategory()
                    onDismiss()
                }
            ) {
                Text(stringResource(id = R.string.add))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}
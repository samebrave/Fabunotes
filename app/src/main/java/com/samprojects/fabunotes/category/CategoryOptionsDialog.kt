package com.samprojects.fabunotes.category

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.samprojects.fabunotes.R
import com.samprojects.fabunotes.data.Category

@Composable
fun CategoryOptionsDialog(
    category: Category,
    onDismiss: () -> Unit,
    onDelete: (Category) -> Unit,
    onUpdate: (Category) -> Unit
) {
    var updatedName by remember { mutableStateOf(category.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.category_options)) },
        text = {
            Column {
                TextField(
                    value = updatedName,
                    onValueChange = { updatedName = it },
                    label = { Text(stringResource(id = R.string.category_name)) }
                )
            }
        },
        confirmButton = {
            Row {
                TextButton(
                    onClick = {
                        onUpdate(category.copy(name = updatedName))
                    }
                ) {
                    Text(stringResource(id = R.string.update))
                }
                TextButton(
                    onClick = { onDelete(category) }
                ) {
                    Text(stringResource(id = R.string.delete))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}
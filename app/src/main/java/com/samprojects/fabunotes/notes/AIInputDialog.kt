package com.samprojects.fabunotes.notes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.samprojects.fabunotes.R

@Composable
fun AIInputDialog(
    onDismiss: () -> Unit,
    onGenerateNote: (String) -> Unit
) {
    var prompt by remember { mutableStateOf("") }

    AlertDialog(
        modifier = aiDialogBorder,
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.generate_ai_note)) },
        text = {
            TextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = { Text(stringResource(R.string.enter_prompt)) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (prompt.isNotEmpty()) {
                        onGenerateNote(prompt)
                    }
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.generate))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
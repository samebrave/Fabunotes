package com.samprojects.fabunotes.notes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samprojects.fabunotes.R

@Composable
fun RecipeDialog(
    onDismiss: () -> Unit,
    onGenerateRecipe: (String) -> Unit
) {
    var ingredients by remember { mutableStateOf("") }

    AlertDialog(
        modifier = aiDialogBorder,
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.generate_recipe)) },
        text = {
            Column {
                Text(stringResource(R.string.enter_ingredients))
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = ingredients,
                    onValueChange = { ingredients = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (ingredients.isNotEmpty()) {
                        onGenerateRecipe(ingredients)
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
package com.samprojects.fabunotes.notes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samprojects.fabunotes.R

@Composable
fun RouteDialog(
    onDismiss: () -> Unit,
    onGenerateRoute: (String, Int) -> Unit
) {
    var destination by remember { mutableStateOf("") }
    var days by remember { mutableStateOf("1") }

    AlertDialog(
        modifier = aiDialogBorder,
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.generate_route)) },
        text = {
            Column {
                Text(stringResource(R.string.enter_destination))
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = destination,
                    onValueChange = { destination = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string.enter_days))
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = days,
                    onValueChange = { days = it.filter { char -> char.isDigit() } },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (destination.isNotEmpty() && days.isNotEmpty()) {
                        onGenerateRoute(destination, days.toInt())
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
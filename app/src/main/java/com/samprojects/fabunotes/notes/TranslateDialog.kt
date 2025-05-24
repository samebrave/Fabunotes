package com.samprojects.fabunotes.notes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslateDialog(
    onDismiss: () -> Unit,
    onTranslate: (String, Language) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf<Language?>(null) }
    var showLanguageSelection by remember { mutableStateOf(false) }

    AlertDialog(
        modifier = aiDialogBorder,
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.translate_text)) },
        text = {
            Column {
                Text(stringResource(R.string.paste_text_to_translate))
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                FilledTonalButton(
                    onClick = { showLanguageSelection = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedLanguage?.displayName ?: stringResource(R.string.select_target_language))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (text.isNotEmpty() && selectedLanguage != null) {
                        onTranslate(text, selectedLanguage!!)
                    }
                    onDismiss()
                },
                enabled = text.isNotEmpty() && selectedLanguage != null
            ) {
                Text(stringResource(R.string.translate))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )

    if (showLanguageSelection) {
        AlertDialog(
            onDismissRequest = { showLanguageSelection = false },
            title = { Text(stringResource(R.string.select_target_language)) },
            text = {
                LazyColumn {
                    items(Language.values()) { language ->
                        TextButton(
                            onClick = {
                                selectedLanguage = language
                                showLanguageSelection = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(language.displayName)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}


enum class Language(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    SPANISH("es", "Español"),
    FRENCH("fr", "Français"),
    GERMAN("de", "Deutsch"),
    ITALIAN("it", "Italiano"),
    PORTUGUESE("pt", "Português"),
    RUSSIAN("ru", "Русский"),
    CHINESE("zh", "中文"),
    JAPANESE("ja", "日本語"),
    KOREAN("ko", "한국어"),
    ARABIC("ar", "العربية"),
    TURKISH("tr", "Türkçe")
}
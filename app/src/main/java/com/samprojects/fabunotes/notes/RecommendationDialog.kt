package com.samprojects.fabunotes.notes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
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
fun RecommendationDialog(
    onDismiss: () -> Unit,
    onGenerateRecommendation: (String, String, String) -> Unit
) {
    var selectedType by remember { mutableStateOf("Book") }
    var selectedGenre by remember { mutableStateOf("") }
    var additionalDetails by remember { mutableStateOf("") }

    val types = listOf("Book", "Movie", "TV Series", "Document")
    val genres = when (selectedType) {
        "Book" -> listOf("Fiction", "Non-fiction", "Mystery", "Science Fiction", "Fantasy", "Romance", "Thriller", "Biography")
        "Movie" -> listOf("Action", "Comedy", "Drama", "Science Fiction", "Horror", "Romance", "Thriller", "Documentary")
        "TV Series" -> listOf("Drama", "Comedy", "Crime", "Science Fiction", "Fantasy", "Action", "Mystery", "Documentary")
        "Document" -> listOf("Academic", "Business", "Technical", "Legal", "Medical", "Historical", "Scientific", "Educational")
        else -> emptyList()
    }

    AlertDialog(
        modifier = aiDialogBorder,
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.get_recommendation)) },
        text = {
            Column {
                Text(stringResource(R.string.select_type))
                LazyRow {
                    items(types) { type ->
                        FilterChip(
                            selected = type == selectedType,
                            onClick = { selectedType = type },
                            label = { Text(type) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string.select_genre))
                LazyRow {
                    items(genres) { genre ->
                        FilterChip(
                            selected = genre == selectedGenre,
                            onClick = { selectedGenre = genre },
                            label = { Text(genre) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = additionalDetails,
                    onValueChange = { additionalDetails = it },
                    label = { Text(stringResource(R.string.additional_details)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedGenre.isNotEmpty()) {
                        onGenerateRecommendation(selectedType, selectedGenre, additionalDetails)
                    }
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.get_recommendation))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
package com.samprojects.fabunotes.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.samprojects.fabunotes.R

@Composable
fun AIToolsDropdownMenu(
    onAIInput: () -> Unit,
    onSummarize: () -> Unit,
    onSimplify: () -> Unit,
    onTranslate: () -> Unit,
    onRecipe: () -> Unit,
    onRoute: () -> Unit,
    onRecommendation: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                Icons.Filled.AutoAwesome,
                contentDescription = "AI Tools",
                modifier = Modifier.size(36.dp)  // Increase from default 24.dp
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.generate_ai_note)) },
                leadingIcon = { Icon(Icons.Default.AutoAwesome, null) },
                onClick = {
                    expanded = false
                    onAIInput()
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.summarize)) },
                leadingIcon = { Icon(Icons.Default.Summarize, null) },
                onClick = {
                    expanded = false
                    onSummarize()
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.simplify)) },
                leadingIcon = { Icon(Icons.Default.Psychology, null) },
                onClick = {
                    expanded = false
                    onSimplify()
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.translate)) },
                leadingIcon = { Icon(Icons.Default.Translate, null) },
                onClick = {
                    expanded = false
                    onTranslate()
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.generate_recipe)) },
                leadingIcon = { Icon(Icons.Default.Restaurant, null) },
                onClick = {
                    expanded = false
                    onRecipe()
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.generate_route)) },
                leadingIcon = { Icon(Icons.Default.Map, null) },
                onClick = {
                    expanded = false
                    onRoute()
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.get_recommendation)) },
                leadingIcon = { Icon(Icons.Default.Recommend, null) },
                onClick = {
                    expanded = false
                    onRecommendation()
                }
            )
        }
    }
}

val aiDialogBorder = Modifier
    .border(
        width = 1.dp,
        color = Color(0xFFFF9800),  // Orange color
        shape = RoundedCornerShape(28.dp)
    )
    .clip(RoundedCornerShape(28.dp))

val topAppBarModifier = Modifier
    .clip(RoundedCornerShape(8.dp))

@Composable
fun getBottomBarContentModifier(): Modifier {
    return Modifier
        .padding(horizontal = 16.dp, vertical = 8.dp) // Add vertical padding here
        .clip(RoundedCornerShape(24.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
        .padding(horizontal = 8.dp) // Remove vertical padding here
}
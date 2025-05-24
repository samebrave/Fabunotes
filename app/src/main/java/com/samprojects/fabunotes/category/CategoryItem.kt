package com.samprojects.fabunotes.category

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.samprojects.fabunotes.R

@Composable
fun CategoryItem(
    categoryItem: CategoryListItem,
    navController: NavController,
    isGridItem: Boolean = true,
    onItemClick: (CategoryListItem) -> Unit,
    onItemLongPress: (CategoryListItem) -> Unit
) {
    val (id, name, color, emoji) = when (categoryItem) {
        is CategoryListItem.RegularCategory -> CategoryInfo(categoryItem.category.id, categoryItem.category.name, categoryItem.category.color, null)
        CategoryListItem.AllNotesCategory -> CategoryInfo(-1, stringResource(id = R.string.all_notes), Color.Blue.toArgb(), "📝")
        CategoryListItem.FavoriteNotesCategory -> CategoryInfo(-2, stringResource(id = R.string.favorite_notes), Color.Red.toArgb(), "⭐")
    }

    // Use the current theme's primary color as a base
    val themeColor = MaterialTheme.colorScheme.primary

    // Adjust the category color based on the theme
    val categoryColor = remember(id, themeColor) {
        when {
            id < 0 -> themeColor // Use theme color for special categories
            else -> {
                val originalColor = Color(color.takeIf { it != 0 } ?: Color.Gray.toArgb())
                // Blend the original color with the theme color
                Color(
                    red = (originalColor.red * 0.3f + themeColor.red * 0.7f),
                    green = (originalColor.green * 0.3f + themeColor.green * 0.7f),
                    blue = (originalColor.blue * 0.3f + themeColor.blue * 0.7f),
                    alpha = 1f
                )
            }
        }
    }

    val gradientBrush = remember(categoryColor) {
        Brush.horizontalGradient(
            colors = listOf(
                categoryColor.copy(alpha = 0.5f),
                categoryColor.copy(alpha = 0.2f),
                categoryColor.copy(alpha = 0.05f)
            )
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isGridItem) Modifier.aspectRatio(1f) else Modifier.height(80.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onItemClick(categoryItem) },
                    onLongPress = { onItemLongPress(categoryItem) }
                )
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (emoji != null) {
                    Text(
                        text = emoji,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Gradient accent line at the bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(gradientBrush)
            )
        }
    }
}

// Define a data class to hold category information
data class CategoryInfo(
    val id: Int,
    val name: String,
    val color: Int,
    val emoji: String?
)
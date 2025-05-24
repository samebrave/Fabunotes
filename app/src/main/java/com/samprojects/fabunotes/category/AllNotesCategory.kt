package com.samprojects.fabunotes.category

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

data class AllNotesCategory(
    val id: Int = -1,
    val name: String = "All Notes",
    val color: Int = Color.Blue.toArgb()
)

package com.samprojects.fabunotes.colorschemes

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun jetBlackColorScheme(): ColorScheme {
    return darkColorScheme(
        primary = Color(0xFF1A1A1A), // Darker grayish black
        onPrimary = Color.White,
        secondary = Color(0xFF444444), // Medium gray for accents
        onSecondary = Color.White,
        tertiary = Color(0xFF6A6A6A), // Light gray for accents
        onTertiary = Color.Black,
        background = Color(0xFF161616), // Dark gray background
        onBackground = Color.White,
        surface = Color(0xFF1A1A1A), // Darker grayish black surface
        onSurface = Color.White
    )
}






package com.samprojects.fabunotes

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.samprojects.fabunotes.category.CategoryListScreen
import com.samprojects.fabunotes.colorschemes.AppTheme
import com.samprojects.fabunotes.colorschemes.jetBlackColorScheme
import com.samprojects.fabunotes.model.NoteViewModel
import com.samprojects.fabunotes.notes.AddNoteScreen
import com.samprojects.fabunotes.notes.AllNotesScreen
import com.samprojects.fabunotes.notes.EditNoteScreen
import com.samprojects.fabunotes.notes.FavoriteNotesScreen
import com.samprojects.fabunotes.notes.NoteDetailScreen
import com.samprojects.fabunotes.notes.NoteListScreen
import com.samprojects.fabunotes.settings.SearchScreen
import com.samprojects.fabunotes.settings.SettingsScreen

@Composable
fun NoteApp() {
    val viewModel: NoteViewModel = viewModel()
    val navController = rememberNavController()
    val systemIsDark = isSystemInDarkTheme()
    val context = LocalContext.current

    val colorScheme = when (viewModel.currentTheme) {
        AppTheme.System -> if (systemIsDark) darkColorScheme() else lightColorScheme()
        AppTheme.Light -> lightColorScheme()
        AppTheme.Dark -> darkColorScheme()
        AppTheme.Material_You -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (systemIsDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        } else {
            if (systemIsDark) darkColorScheme() else lightColorScheme()
        }
        AppTheme.Jet_Black -> jetBlackColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = "categoryList"
            ) {
                composable("categoryList") {
                    CategoryListScreen(viewModel, navController)
                }
                composable(
                    "noteList/{categoryId}",
                    arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: -1
                    NoteListScreen(viewModel, navController, categoryId)
                }
                composable(
                    "editNote/{noteId}",
                    arguments = listOf(navArgument("noteId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
                    EditNoteScreen(viewModel, noteId, navController)
                }
                composable(
                    "addNote/{categoryId}",
                    arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: -1
                    AddNoteScreen(viewModel, navController, categoryId)
                }
                composable(
                    "noteDetail/{noteId}",
                    arguments = listOf(navArgument("noteId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
                    NoteDetailScreen(viewModel, noteId, navController)
                }
                composable("allNotes") {
                    AllNotesScreen(viewModel, navController)
                }
                composable("favoriteNotes") {
                    FavoriteNotesScreen(viewModel, navController)
                }
                composable("settings") {
                    SettingsScreen(viewModel, navController)
                }
                composable("search") {
                    SearchScreen(viewModel, navController)
                }
            }
        }
    }
}
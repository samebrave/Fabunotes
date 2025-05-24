package com.samprojects.fabunotes.category

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.samprojects.fabunotes.R
import com.samprojects.fabunotes.data.Category
import com.samprojects.fabunotes.model.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(viewModel: NoteViewModel, navController: NavController) {
    val categories by viewModel.allCategories.collectAsState(initial = emptyList())
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var showCategoryOptionsDialog by remember { mutableStateOf(false) }

    val categoriesWithSpecial = remember(categories) {
        listOf(
            CategoryListItem.AllNotesCategory,
            CategoryListItem.FavoriteNotesCategory
        ) + categories.map { CategoryListItem.RegularCategory(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.categories)) },
                actions = {
                    IconButton(onClick = { navController.navigate("search") }) {
                        Icon(Icons.Default.Search, contentDescription = stringResource(id = R.string.search))
                    }
                    IconButton(onClick = { viewModel.toggleCategoryViewMode() }) {
                        Icon(
                            imageVector = if (viewModel.isCategoryGridView) Icons.Default.List else Icons.Default.GridView,
                            contentDescription = stringResource(id = R.string.toggle_view_mode)
                        )
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(id = R.string.settings))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddCategoryDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.add_category))
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            if (viewModel.isCategoryGridView) {
                CategoryGrid(
                    categories = categoriesWithSpecial,
                    navController = navController,
                    paddingValues = paddingValues,
                    onItemClick = { item ->
                        when (item) {
                            is CategoryListItem.RegularCategory -> navController.navigate("noteList/${item.category.id}")
                            CategoryListItem.AllNotesCategory -> navController.navigate("allNotes")
                            CategoryListItem.FavoriteNotesCategory -> navController.navigate("favoriteNotes")
                        }
                    },
                    onItemLongPress = { item ->
                        if (item is CategoryListItem.RegularCategory) {
                            selectedCategory = item.category
                            showCategoryOptionsDialog = true
                        }
                    }
                )
            } else {
                CategoryList(
                    categories = categoriesWithSpecial,
                    navController = navController,
                    paddingValues = paddingValues,
                    onItemClick = { item ->
                        when (item) {
                            is CategoryListItem.RegularCategory -> navController.navigate("noteList/${item.category.id}")
                            CategoryListItem.AllNotesCategory -> navController.navigate("allNotes")
                            CategoryListItem.FavoriteNotesCategory -> navController.navigate("favoriteNotes")
                        }
                    },
                    onItemLongPress = { item ->
                        if (item is CategoryListItem.RegularCategory) {
                            selectedCategory = item.category
                            showCategoryOptionsDialog = true
                        }
                    }
                )
            }
        }
    }

    if (showAddCategoryDialog) {
        AddCategoryDialog(
            viewModel = viewModel,
            onDismiss = { showAddCategoryDialog = false }
        )
    }

    if (showCategoryOptionsDialog) {
        CategoryOptionsDialog(
            category = selectedCategory!!,
            onDismiss = { showCategoryOptionsDialog = false },
            onDelete = {
                viewModel.deleteCategory(it)
                showCategoryOptionsDialog = false
            },
            onUpdate = { updatedCategory ->
                viewModel.updateCategory(updatedCategory)
                showCategoryOptionsDialog = false
            }
        )
    }
}
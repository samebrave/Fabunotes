package com.samprojects.fabunotes.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CategoryList(
    categories: List<CategoryListItem>,
    navController: androidx.navigation.NavController,
    paddingValues: PaddingValues,
    onItemClick: (CategoryListItem) -> Unit,
    onItemLongPress: (CategoryListItem) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        items(categories) { categoryItem ->
            CategoryItem(
                categoryItem = categoryItem,
                navController = navController,
                isGridItem = false,
                onItemClick = onItemClick,
                onItemLongPress = onItemLongPress
            )
        }
    }
}
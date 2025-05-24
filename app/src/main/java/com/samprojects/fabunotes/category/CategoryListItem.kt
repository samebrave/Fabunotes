package com.samprojects.fabunotes.category

import com.samprojects.fabunotes.data.Category

sealed class CategoryListItem {
    data class RegularCategory(val category: Category) : CategoryListItem()
    object AllNotesCategory : CategoryListItem()
    object FavoriteNotesCategory : CategoryListItem()
}
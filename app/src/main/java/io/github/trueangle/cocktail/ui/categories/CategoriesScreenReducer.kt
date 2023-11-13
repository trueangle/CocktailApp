package io.github.trueangle.cocktail.ui.categories

import io.github.trueangle.cocktail.domain.model.Category
import io.github.trueangle.cocktail.domain.model.RequestException
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

class CategoriesScreenReducer {
    fun reduceCategories(viewState: CategoriesState, categories: List<Category>) = viewState.copy(
        progress = false,
        error = null,
        list = categories.toPersistentList()
    )

    fun reduceError(viewState: CategoriesState, e: RequestException) = viewState.copy(
        progress = false,
        error = e,
        list = persistentListOf()
    )
}
package io.github.trueangle.cocktail.ui.categories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.result.onFailure
import com.github.kittinunf.result.onSuccess
import io.github.trueangle.cocktail.domain.model.Category
import io.github.trueangle.cocktail.domain.model.Drink
import io.github.trueangle.cocktail.domain.model.RequestException
import io.github.trueangle.cocktail.domain.repository.CategoryRepository
import io.github.trueangle.cocktail.domain.repository.DrinkRepository
import io.github.trueangle.cocktail.util.Intent
import io.github.trueangle.cocktail.util.Effect
import io.github.trueangle.cocktail.util.MviViewModel
import io.github.trueangle.cocktail.util.State
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class CategoriesState(
    val progress: Boolean = true,
    val list: ImmutableList<Category> = persistentListOf(),
    val error: RequestException? = null,
) : State

sealed interface CategoriesIntent : Intent {
    data object OnRetry : CategoriesIntent
}

sealed interface CategoriesEffect : Effect

class CategoriesViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val categoryRepository: CategoryRepository,
    private val reducer: CategoriesScreenReducer = CategoriesScreenReducer(),
    override val initialState: CategoriesState = CategoriesState(),
) : MviViewModel<CategoriesState, CategoriesIntent, CategoriesEffect>() {

    init {
        loadData()
    }

    override fun dispatch(intent: CategoriesIntent) {
        when (intent) {
            CategoriesIntent.OnRetry -> loadData()
        }
    }

    private fun loadData() {
        viewState = viewState.copy(progress = true, error = null)
        viewModelScope.launch(Dispatchers.IO) {

            categoryRepository.getCategories()
                .onSuccess {
                    viewState = reducer.reduceCategories(viewState, it)
                }.onFailure {
                    viewState = reducer.reduceError(viewState, it)
                }
        }
    }
}
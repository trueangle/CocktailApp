package io.github.trueangle.cocktail.ui.categories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.result.onFailure
import com.github.kittinunf.result.onSuccess
import io.github.trueangle.cocktail.domain.model.Category
import io.github.trueangle.cocktail.domain.model.RequestException
import io.github.trueangle.cocktail.domain.repository.DrinkRepository
import io.github.trueangle.cocktail.util.Action
import io.github.trueangle.cocktail.util.Effect
import io.github.trueangle.cocktail.util.MviViewModel
import io.github.trueangle.cocktail.util.State
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class CategoriesState(
    val progress: Boolean = true,
    val list: ImmutableList<Category> = persistentListOf(),
    val error: RequestException? = null
) : State

sealed interface CategoriesAction : Action {

}

sealed interface CategoriesEffect : Effect {

}

class CategoriesViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val drinkRepository: DrinkRepository,
    private val reducer: CategoriesScreenReducer,
) : MviViewModel<CategoriesState, CategoriesAction, CategoriesEffect>() {

    override val viewState: StateFlow<CategoriesState> = MutableStateFlow(CategoriesState())
    init {
        loadData()
    }

    override fun dispatch(action: CategoriesAction) {
        TODO("Not yet implemented")
    }

    private fun loadData() {
        viewModelScope.launch {

            withContext(Dispatchers.IO) {
                drinkRepository.getCategories()
            }.onSuccess {

            }.onFailure {

            }
        }
    }
}
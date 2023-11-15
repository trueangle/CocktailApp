package io.github.trueangle.cocktail.ui.drinks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.result.onFailure
import com.github.kittinunf.result.onSuccess
import io.github.trueangle.cocktail.domain.model.Category
import io.github.trueangle.cocktail.domain.model.Drink
import io.github.trueangle.cocktail.domain.model.RequestException
import io.github.trueangle.cocktail.domain.repository.DrinkRepository
import io.github.trueangle.cocktail.util.Effect
import io.github.trueangle.cocktail.util.Intent
import io.github.trueangle.cocktail.util.MviViewModel
import io.github.trueangle.cocktail.util.State
import io.github.trueangle.cocktail.util.decodeUrl
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class DrinksState(
    val categoryName: String,
    val progress: Boolean = true,
    val list: ImmutableList<Drink> = persistentListOf(),
    val error: RequestException? = null
) : State

sealed interface DrinksIntent : Intent {
    data object OnRetry : DrinksIntent
}

sealed interface DrinksEffect : Effect {

}

class DrinksViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val drinkRepository: DrinkRepository
) : MviViewModel<DrinksState, DrinksIntent, DrinksEffect>() {

    override fun setInitialState(): DrinksState {
        val categoryName = requireNotNull(savedStateHandle["categoryName"]) as String
        return DrinksState(categoryName = categoryName.decodeUrl())
    }

    init {
        getDrinks()
    }

    override fun dispatch(intent: DrinksIntent) {
        when (intent) {
            DrinksIntent.OnRetry -> getDrinks()
        }
    }

    private fun getDrinks() {
        viewState = viewState.copy(progress = true, error = null)
        viewModelScope.launch(Dispatchers.IO) {
            drinkRepository.getByCategoryName(viewState.categoryName)
                .onSuccess {
                    viewState =
                        viewState.copy(progress = false, error = null, list = it.toPersistentList())
                }.onFailure {
                    viewState = viewState.copy(progress = false, error = it)
                }
        }
    }
}
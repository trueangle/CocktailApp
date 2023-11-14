package io.github.trueangle.cocktail.ui.favorites

import androidx.lifecycle.viewModelScope
import io.github.trueangle.cocktail.domain.model.Drink
import io.github.trueangle.cocktail.domain.repository.DrinkRepository
import io.github.trueangle.cocktail.util.Effect
import io.github.trueangle.cocktail.util.Intent
import io.github.trueangle.cocktail.util.MviViewModel
import io.github.trueangle.cocktail.util.State
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class FavoritesState(
    val list: ImmutableList<Drink> = persistentListOf(),
) : State

sealed interface DrinksIntent : Intent

sealed interface DrinksEffect : Effect

class FavoritesViewModel(
    private val drinkRepository: DrinkRepository
) : MviViewModel<FavoritesState, DrinksIntent, DrinksEffect>() {

    override fun setInitialState(): FavoritesState = FavoritesState()

    init {
        getFavorites()
    }

    override fun dispatch(intent: DrinksIntent) {}

    private fun getFavorites() {
        drinkRepository
            .getFavorites()
            .onEach { drinks ->
                viewState = viewState.copy(list = drinks?.toPersistentList() ?: persistentListOf())
            }
            .launchIn(viewModelScope)
    }
}
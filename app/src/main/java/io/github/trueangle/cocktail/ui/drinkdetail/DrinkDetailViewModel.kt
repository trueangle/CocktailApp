package io.github.trueangle.cocktail.ui.drinkdetail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.result.onFailure
import com.github.kittinunf.result.onSuccess
import io.github.trueangle.cocktail.domain.model.Drink
import io.github.trueangle.cocktail.domain.model.RequestException
import io.github.trueangle.cocktail.domain.repository.DrinkRepository
import io.github.trueangle.cocktail.util.Effect
import io.github.trueangle.cocktail.util.Intent
import io.github.trueangle.cocktail.util.MviViewModel
import io.github.trueangle.cocktail.util.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

data class DrinkDetailState(
    val id: String,
    val progress: Boolean = true,
    val drink: Drink? = null,
    val error: RequestException? = null
) : State

sealed interface DrinkDetailIntent : Intent {
    data object OnFavoriteClick : DrinkDetailIntent
    data object OnRetry : DrinkDetailIntent
}

sealed interface DrinkDetailEffect : Effect {
    class AddedToFavorites(val isAdded: Boolean) : DrinkDetailEffect
}

class DrinkDetailViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val drinkRepository: DrinkRepository
) : MviViewModel<DrinkDetailState, DrinkDetailIntent, DrinkDetailEffect>() {

    private var job: Job? = null

    override val initialState = DrinkDetailState(requireNotNull(savedStateHandle["id"]))

    init {
        getDrink()
    }

    override fun dispatch(intent: DrinkDetailIntent) {
        when (intent) {
            DrinkDetailIntent.OnFavoriteClick -> viewModelScope.launch {
                val drink = viewState.drink ?: return@launch
                val favorite = !drink.favorite
                drinkRepository.update(drink.copy(favorite = favorite))
                effectChannel.trySend(DrinkDetailEffect.AddedToFavorites(favorite))
            }

            DrinkDetailIntent.OnRetry -> getDrink()
        }
    }

    private fun getDrink() {
        job?.cancel()
        job = viewModelScope.launch {
            viewState = viewState.copy(progress = true, error = null)

            drinkRepository
                .getById(viewState.id)
                .flowOn(Dispatchers.IO)
                .collectLatest { result ->
                    Log.d("collect", result.toString())

                    result.onSuccess {
                        viewState =
                            viewState.copy(progress = false, error = null, drink = it)
                    }.onFailure {
                        it.cause?.printStackTrace()
                        viewState = viewState.copy(progress = false, error = it)
                    }
                }
        }
    }

    override fun onCleared() {
        job?.cancel()
    }
}
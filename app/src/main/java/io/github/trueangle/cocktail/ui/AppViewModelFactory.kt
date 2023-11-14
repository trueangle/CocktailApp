package io.github.trueangle.cocktail.ui

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import io.github.trueangle.cocktail.domain.repository.DrinkRepository
import io.github.trueangle.cocktail.ui.categories.CategoriesViewModel
import io.github.trueangle.cocktail.ui.drinkdetail.DrinkDetailViewModel
import io.github.trueangle.cocktail.ui.drinks.DrinksViewModel
import io.github.trueangle.cocktail.ui.favorites.FavoritesViewModel
import javax.inject.Inject
import javax.inject.Singleton

@[Singleton Immutable]
class AppViewModelFactory @Inject constructor(
    private val drinkRepository: DrinkRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T = when {
        modelClass.isAssignableFrom(DrinksViewModel::class.java) -> DrinksViewModel(
            savedStateHandle = extras.createSavedStateHandle(),
            drinkRepository = drinkRepository
        ) as T

        modelClass.isAssignableFrom(DrinkDetailViewModel::class.java) -> DrinkDetailViewModel(
            savedStateHandle = extras.createSavedStateHandle(),
            drinkRepository = drinkRepository
        ) as T

        modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> FavoritesViewModel(
            drinkRepository = drinkRepository
        ) as T

        else -> CategoriesViewModel(
            savedStateHandle = extras.createSavedStateHandle(),
            drinkRepository = drinkRepository
        ) as T
    }
}
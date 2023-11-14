package io.github.trueangle.cocktail.domain.repository

import com.github.kittinunf.result.Result
import io.github.trueangle.cocktail.domain.model.Category
import io.github.trueangle.cocktail.domain.model.Drink
import io.github.trueangle.cocktail.domain.model.RequestException
import kotlinx.coroutines.flow.Flow

interface DrinkRepository {
    suspend fun getCategories(): Result<List<Category>, RequestException>

    suspend fun getDrinksByCategoryName(name: String): Result<List<Drink>, RequestException>

    fun getDrinkById(id: String): Flow<Result<Drink, RequestException>>
    suspend fun updateDrink(drink: Drink)
}
package io.github.trueangle.cocktail.domain.repository

import com.github.kittinunf.result.Result
import io.github.trueangle.cocktail.domain.model.Category
import io.github.trueangle.cocktail.domain.model.Drink
import io.github.trueangle.cocktail.domain.model.RequestException
import io.github.trueangle.cocktail.domain.model.SearchSource
import kotlinx.coroutines.flow.Flow

interface DrinkRepository {
    suspend fun getCategories(): Result<List<Category>, RequestException>

    suspend fun getByCategoryName(name: String): Result<List<Drink>, RequestException>

    fun getById(id: String): Flow<Result<Drink, RequestException>>

    fun getFavorites(): Flow<List<Drink>?>

    suspend fun update(drink: Drink)

    suspend fun searchByName(query: String, source: SearchSource): Result<List<Drink>, RequestException>
}
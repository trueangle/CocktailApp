package io.github.trueangle.cocktail.domain.repository

import com.github.kittinunf.result.Result
import io.github.trueangle.cocktail.domain.model.Category
import io.github.trueangle.cocktail.domain.model.RequestException

interface CategoryRepository {
    suspend fun getCategories(): Result<List<Category>, RequestException>
}
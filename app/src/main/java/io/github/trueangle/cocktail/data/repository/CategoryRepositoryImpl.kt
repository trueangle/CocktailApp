package io.github.trueangle.cocktail.data.repository

import io.github.trueangle.cocktail.data.DrinksApi
import io.github.trueangle.cocktail.data.api.model.toDomainModels
import io.github.trueangle.cocktail.domain.repository.CategoryRepository
import io.github.trueangle.cocktail.util.resultBodyOf
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val api: DrinksApi
): CategoryRepository{
    override suspend fun getCategories() = resultBodyOf {
        api.getCategories().toDomainModels()
    }
}
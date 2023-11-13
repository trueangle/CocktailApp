package io.github.trueangle.cocktail.data

import io.github.trueangle.cocktail.data.api.model.toDomainModels
import io.github.trueangle.cocktail.domain.repository.DrinkRepository
import io.github.trueangle.cocktail.util.resultBodyOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DrinkRepositoryImpl @Inject constructor(private val api: DrinksApi) : DrinkRepository {
    override suspend fun getCategories() = resultBodyOf {
        api.getCategories().toDomainModels()
    }

    override suspend fun getDrinksByCategoryName(name: String) =
        resultBodyOf {
            api.getDrinksByCategoryName(name).toDomainModels()
        }

}


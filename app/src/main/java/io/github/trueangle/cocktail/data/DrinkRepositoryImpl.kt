package io.github.trueangle.cocktail.data

import android.util.Log
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.isSuccess
import com.github.kittinunf.result.map
import io.github.trueangle.cocktail.data.api.model.toDomainModels
import io.github.trueangle.cocktail.data.api.model.toEntity
import io.github.trueangle.cocktail.data.db.DrinkDao
import io.github.trueangle.cocktail.data.db.entity.DrinkEntity
import io.github.trueangle.cocktail.data.db.entity.toDomainModel
import io.github.trueangle.cocktail.data.db.entity.toEntity
import io.github.trueangle.cocktail.domain.model.Drink
import io.github.trueangle.cocktail.domain.model.RequestException
import io.github.trueangle.cocktail.domain.repository.DrinkRepository
import io.github.trueangle.cocktail.util.resultBodyOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DrinkRepositoryImpl @Inject constructor(
    private val api: DrinksApi,
    private val drinkDao: DrinkDao
) : DrinkRepository {
    override suspend fun getCategories() = resultBodyOf {
        api.getCategories().toDomainModels()
    }

    override suspend fun getDrinksByCategoryName(name: String) =
        resultBodyOf {
            api.getDrinksByCategoryName(name).toDomainModels(categoryName = name)
        }

    override suspend fun updateDrink(drink: Drink) {
        withContext(Dispatchers.IO) {
            drinkDao.insert(drink.toEntity())
        }
    }

    override fun getDrinkById(id: String): Flow<Result<Drink, RequestException>> =
        drinkDao
            .getById(id)
            .flatMapLatest {
                Log.d("Entity", it.toString())

                if (it != null) {
                    flow {
                        emit(Result.of { it.toDomainModel() })
                    }
                } else {
                    val networkResult = getDrinkFromServer(id)

                    if (networkResult.isSuccess()) {
                        drinkDao.insert(networkResult.value)
                    }

                    flow {
                        emit(networkResult.map { drinkEntity -> drinkEntity.toDomainModel() })
                    }
                }
            }

    private suspend fun getDrinkFromServer(id: String): Result<DrinkEntity, RequestException> =
        resultBodyOf {
            api
                .getDrinkById(id)
                .drinks
                .first()
                .toEntity()
        }
}


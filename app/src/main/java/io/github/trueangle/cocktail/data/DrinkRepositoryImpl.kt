package io.github.trueangle.cocktail.data

import android.util.Log
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.isSuccess
import com.github.kittinunf.result.map
import io.github.trueangle.cocktail.data.api.model.toDomainModels
import io.github.trueangle.cocktail.data.api.model.toEntityList
import io.github.trueangle.cocktail.data.db.DrinkDao
import io.github.trueangle.cocktail.data.db.entity.DrinkEntity
import io.github.trueangle.cocktail.data.db.entity.toDomainModel
import io.github.trueangle.cocktail.data.db.entity.toEntity
import io.github.trueangle.cocktail.domain.model.Drink
import io.github.trueangle.cocktail.domain.model.RequestException
import io.github.trueangle.cocktail.domain.model.SearchSource
import io.github.trueangle.cocktail.domain.repository.DrinkRepository
import io.github.trueangle.cocktail.util.resultBodyOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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

    override suspend fun getByCategoryName(name: String) =
        resultBodyOf {
            api.getDrinksByCategoryName(name).toDomainModels(categoryName = name)
        }

    override suspend fun update(drink: Drink) {
        withContext(Dispatchers.IO) {
            drinkDao.insert(drink.toEntity())
        }
    }

    override suspend fun searchByName(
        query: String,
        source: SearchSource
    ): Result<List<Drink>, RequestException> = when (source) {
        SearchSource.NETWORK -> resultBodyOf {
            api
                .searchByName(query)
                .toEntityList()
                .map { it.toDomainModel() }
        }

        SearchSource.LOCAL -> Result.of { drinkDao.searchByName(query).map { it.toDomainModel() } }
    }

    override fun getById(id: String): Flow<Result<Drink, RequestException>> =
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

    override fun getFavorites(): Flow<List<Drink>?> =
        drinkDao
            .getFavorites()
            .map { entities -> entities?.map { it.toDomainModel() } }

    private suspend fun getDrinkFromServer(id: String): Result<DrinkEntity, RequestException> =
        resultBodyOf {
            api
                .getDrinkById(id)
                .toEntityList()
                .first()
        }
}


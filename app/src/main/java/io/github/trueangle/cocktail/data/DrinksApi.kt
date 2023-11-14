package io.github.trueangle.cocktail.data

import io.github.trueangle.cocktail.data.api.model.CategoriesResponse
import io.github.trueangle.cocktail.data.api.model.DrinkDetailResponse
import io.github.trueangle.cocktail.data.api.model.DrinksResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DrinksApi {
    @GET("v1/1/list.php?c=list")
    suspend fun getCategories(): CategoriesResponse

    @GET("v1/1/filter.php")
    suspend fun getDrinksByCategoryName(@Query("c") name: String): DrinksResponse

    @GET("v1/1/lookup.php")
    suspend fun getDrinkById(@Query("i") id: String): DrinkDetailResponse
}
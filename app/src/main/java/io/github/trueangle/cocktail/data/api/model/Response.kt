package io.github.trueangle.cocktail.data.api.model

import io.github.trueangle.cocktail.domain.model.Category
import io.github.trueangle.cocktail.domain.model.Drink
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoriesResponse(
    @SerialName("drinks")
    val categories: List<CategoryResponse>
)

@Serializable
data class CategoryResponse(
    @SerialName("strCategory")
    val name: String
)

@Serializable
data class DrinkResponse(
    @SerialName("idDrink")
    val id: String,
    @SerialName("strDrink")
    val name: String,
    @SerialName("strDrinkThumb")
    val thumbUrl: String,
)

@Serializable
data class DrinksResponse(
    val drinks: List<DrinkResponse>
)

fun CategoriesResponse.toDomainModels() = categories.map {
    it.toDomainModel()
}

fun DrinksResponse.toDomainModels() = drinks.map { it.toDomainModel() }

fun CategoryResponse.toDomainModel() = Category(
    name = name
)

fun DrinkResponse.toDomainModel() = Drink(
    name = name,
    thumbUrl = thumbUrl,
    id = id
)
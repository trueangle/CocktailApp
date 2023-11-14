package io.github.trueangle.cocktail.data.api.model

import io.github.trueangle.cocktail.data.db.entity.DrinkEntity
import io.github.trueangle.cocktail.data.db.entity.IngredientDto
import io.github.trueangle.cocktail.domain.model.Drink
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DrinkDetailResponse(
    @SerialName("drinks")
    val drinks: List<Map<String, String?>>
)

@Serializable
data class DrinksResponse(
    val drinks: List<DrinkResponse>
)

fun DrinksResponse.toDomainModels(categoryName: String) =
    drinks.map { it.toDomainModel(categoryName) }

fun DrinkResponse.toDomainModel(categoryName: String) = Drink(
    id = id,
    thumbUrl = thumbUrl,
    name = name,
    category = categoryName,
    glass = null,
    favorite = false,
    ingredients = persistentListOf(),
    instructions = null,
    imageSource = null,
    type = null
)

fun Map<String, String?>.toEntity(): DrinkEntity {
    val ingredientList = entries
        .asSequence()
        .filter { it.key.startsWith("strIngredient") && it.value != null }
        .map { ingredientEntry ->
            val ingredientIndex = ingredientEntry.key.removePrefix("strIngredient").toIntOrNull()
            val measureKey = "strMeasure$ingredientIndex"
            val measure = get(measureKey)

            IngredientDto(ingredientEntry.value!!, measure ?: "")
        }.toList()

    return DrinkEntity(
        id = requireNotNull(get("idDrink")),
        thumbUrl = get("strDrinkThumb").orEmpty(),
        name = get("strDrink").orEmpty(),
        category = get("strCategory").orEmpty(),
        glass = get("strGlass"),
        instructions = get("strInstructions"),
        ingredients = ingredientList,
        imageSource = get("strImageSource"),
        favorite = false,
        type = get("strAlcoholic")
    )
}
package io.github.trueangle.cocktail.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.trueangle.cocktail.domain.model.Drink
import io.github.trueangle.cocktail.domain.model.Ingredient
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity("drinks")
data class DrinkEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "thumbUrl")
    val thumbUrl: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "glass")
    val glass: String?,
    @ColumnInfo(name = "favorite")
    val favorite: Boolean,
    @ColumnInfo(name = "instructions")
    val instructions: String?,
    @ColumnInfo(name = "imageSource")
    val imageSource: String?,
    @ColumnInfo(name = "type")
    val type: String?,
    @ColumnInfo(name = "ingredientsJson")
    val ingredients: List<IngredientDto>
)

@Serializable
data class IngredientDto(
    @SerialName("name")
    val name: String,
    @SerialName("measure")
    val measure: String
)

fun IngredientDto.toDomainModel() = Ingredient(name, measure)

fun DrinkEntity.toDomainModel() = Drink(
    id = id,
    thumbUrl = thumbUrl,
    name = name,
    category = category,
    glass = glass,
    favorite = favorite,
    ingredients = ingredients.map { it.toDomainModel() }.toImmutableList(),
    instructions = instructions,
    imageSource = imageSource,
    type = type
)

fun Drink.toEntity() = DrinkEntity(
    id = id,
    thumbUrl = thumbUrl,
    name = name,
    category = category,
    glass = glass,
    favorite = favorite,
    ingredients = ingredients.map { IngredientDto(it.name, it.measure) },
    instructions = instructions,
    imageSource = imageSource,
    type = type
)
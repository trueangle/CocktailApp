package io.github.trueangle.cocktail.domain.model

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList

@Stable
data class Drink(
    val id: String,
    val thumbUrl: String,
    val name: String,
    val category: String,
    val glass: String?,
    val favorite: Boolean,
    val ingredients: ImmutableList<Ingredient>,
    val instructions: String?,
    val imageSource: String?,
    val type: String?
)

@Stable
data class Ingredient(
    val name: String,
    val measure: String
)
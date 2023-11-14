package io.github.trueangle.cocktail.domain.model

import kotlinx.collections.immutable.ImmutableList

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

data class Ingredient(
    val name: String,
    val measure: String
)
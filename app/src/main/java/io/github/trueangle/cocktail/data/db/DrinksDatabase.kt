package io.github.trueangle.cocktail.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import io.github.trueangle.cocktail.data.db.entity.DrinkEntity
import io.github.trueangle.cocktail.data.db.entity.IngredientDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Database(
    entities = [DrinkEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomTypeConverter::class)
abstract class DrinksDatabase : RoomDatabase() {
    abstract fun drinkDao(): DrinkDao
}

object RoomTypeConverter {
    private val json = Json { encodeDefaults = true }

    @TypeConverter
    @JvmStatic
    fun toJson(ingredients: List<IngredientDto>): String = json.encodeToString(ingredients)

    @TypeConverter
    @JvmStatic
    fun fromJson(string: String): List<IngredientDto> = json.decodeFromString(string)
}

package io.github.trueangle.cocktail.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.trueangle.cocktail.data.db.entity.DrinkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DrinkDao {
    @Query("SELECT * FROM drinks WHERE id ==:id")
    fun getById(id: String): Flow<DrinkEntity?>

    @Query("SELECT * FROM drinks WHERE favorite == 1")
    fun getFavorites(): Flow<List<DrinkEntity>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(drinkEntity: DrinkEntity)

    @Query("SELECT * FROM drinks WHERE favorite == 1 AND name LIKE '%' || :query || '%'")
    fun searchByName(query: String): List<DrinkEntity>
}
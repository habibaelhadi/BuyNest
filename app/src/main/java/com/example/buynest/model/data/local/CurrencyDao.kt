package com.example.buynest.model.data.local

import androidx.room.*

@Dao
interface CurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrency(data: CurrencyEntity)

    @Query("SELECT * FROM currency_table WHERE base = :base LIMIT 1")
    suspend fun getCurrency(base: String): CurrencyEntity?
}
package com.example.buynest.model.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [CurrencyEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(CurrencyConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
}
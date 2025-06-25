package com.example.buynest.model.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_table")
data class CurrencyEntity(
    @PrimaryKey val base: String,
    val rates: Map<String, Double>,
    val lastUpdated: Long
)
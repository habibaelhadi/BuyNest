package com.example.buynest.repository.currency.datasource

import android.content.Context
import com.example.buynest.model.data.local.CurrencyEntity

interface ICurrencyDataSource {
    suspend fun getFetchTargetRate(context: Context): Double
    suspend fun fetchAndStoreRates(context: Context): CurrencyEntity?
    suspend fun getLocalRates(base: String): CurrencyEntity?
}

package com.example.buynest.repository.currency

import android.content.Context
import com.example.buynest.model.data.local.CurrencyEntity

interface ICurrencyRepository {
    suspend fun getExchangeRate(context: Context): Double
    suspend fun getCurrencyRates(base: String = "EGP", context: Context): CurrencyEntity?
}

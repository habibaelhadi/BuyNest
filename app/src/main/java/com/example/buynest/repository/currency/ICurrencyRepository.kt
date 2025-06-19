package com.example.buynest.repository.currency

import android.content.Context

interface ICurrencyRepository {
    suspend fun getExchangeRate(context: Context): Double
}

package com.example.buynest.repository.currency

import android.content.Context
import com.example.buynest.model.data.local.CurrencyEntity
import com.example.buynest.repository.currency.datasource.ICurrencyDataSource

class CurrencyRepositoryImpl(
    private val currencyDataSource: ICurrencyDataSource
) : ICurrencyRepository {

    private val refreshIntervalMs = 24 * 60 * 60 * 1000L

    override suspend fun getExchangeRate(context: Context): Double {
        return currencyDataSource.getFetchTargetRate(context)
    }

    override suspend fun getCurrencyRates(base: String, context: Context): CurrencyEntity? {
        val local = currencyDataSource.getLocalRates(base)
        val now = System.currentTimeMillis()

        return if (local == null || now - local.lastUpdated >= refreshIntervalMs) {
            currencyDataSource.fetchAndStoreRates(context)
        } else {
            local
        }
    }
}

package com.example.buynest.repository.currency.datasource

import android.content.Context
import com.example.buynest.BuildConfig
import com.example.buynest.model.data.local.CurrencyDao
import com.example.buynest.model.data.local.CurrencyEntity
import com.example.buynest.model.data.remote.rest.ExchangeRateClient
import com.example.buynest.utils.SharedPrefHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class CurrencyDataSourceImpl(
    private val dao: CurrencyDao
) : ICurrencyDataSource {

    override suspend fun getFetchTargetRate(context: Context): Double = withContext(Dispatchers.IO) {
        try {
            val targetCurrency = SharedPrefHelper.getCurrency(context)
            val response = ExchangeRateClient.api.getRates(
                apiKey = BuildConfig.CRUUENCY_API_KEY,
                base = "EGP"
            )
            response.conversion_rates[targetCurrency] ?: 0.0
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }

    fun filterSelectedRates(allRates: Map<String, Double>): Map<String, Double> {
        val allowedCurrencies = listOf("EGP", "USD", "EUR", "SAR", "GBP")
        return allRates.filterKeys { it in allowedCurrencies }
    }

    override suspend fun fetchAndStoreRates(context: Context): CurrencyEntity? = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = ExchangeRateClient.api.getRates(
                apiKey = BuildConfig.CRUUENCY_API_KEY,
                base = "EGP"
            )
            val filteredRates = filterSelectedRates(response.conversion_rates)

            val entity = CurrencyEntity(
                base = response.base_code,
                rates = filteredRates,
                lastUpdated = System.currentTimeMillis()
            )
            dao.insertCurrency(entity)
            entity
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getLocalRates(base: String): CurrencyEntity? = withContext(Dispatchers.IO) {
        dao.getCurrency(base)
    }
}

package com.example.buynest.model.data.remote.rest

import android.content.Context
import com.example.buynest.BuildConfig
import com.example.buynest.utils.SharedPrefHelper
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class RemoteDataSourceImpl(
    private val stripeAPI: StripeAPI
) : IRemoteDataSource {

    override suspend fun createPaymentIntent(
        amount: Int,
        currency: String,
        paymentMethod: String
    ): Response<JsonObject> {
        return stripeAPI.createPaymentIntent(amount, currency, paymentMethod)
    }

    override suspend fun getUsdToTargetRate(context: Context): Double = withContext(Dispatchers.IO) {
        try {
            val targetCurrency = SharedPrefHelper.getCurrency(context)
            val response = ExchangeRateClient.api.getRates(
                apiKey = BuildConfig.CRUUENCY_API_KEY,
                base = "USD"
            )
            response.conversion_rates[targetCurrency] ?: 0.0
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }

}

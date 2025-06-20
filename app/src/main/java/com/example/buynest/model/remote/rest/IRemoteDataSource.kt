package com.example.buynest.model.remote.rest

import android.content.Context
import com.google.gson.JsonObject
import retrofit2.Response

interface IRemoteDataSource {
    suspend fun createPaymentIntent(
        amount: Int,
        currency: String,
        paymentMethod: String = "card"
    ): Response<JsonObject>

    suspend fun getUsdToTargetRate(context: Context): Double

}

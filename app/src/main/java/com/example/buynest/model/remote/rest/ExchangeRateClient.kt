package com.example.buynest.model.remote.rest

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ExchangeRateClient {
    private const val BASE_URL = "https://v6.exchangerate-api.com/"

    val api: ExchangeRateApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExchangeRateApi::class.java)
    }
}

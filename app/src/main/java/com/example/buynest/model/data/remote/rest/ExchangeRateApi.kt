package com.example.buynest.model.data.remote.rest

import com.example.buynest.model.state.ExchangeRateResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ExchangeRateApi {
    @GET("v6/{apiKey}/latest/{base}")
    suspend fun getRates(
        @Path("apiKey") apiKey: String,
        @Path("base") base: String,
        @Query("symbols") symbols: String = "EGP,USD,EUR,SAR,GBP"
    ): ExchangeRateResponse
}

package com.example.buynest.model.remote.rest

import com.example.buynest.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object StripeClient {
    private const val STRIPE_BASE_URL = "https://api.stripe.com/v1/"

    private const val STRIPE_SECRET_KEY = BuildConfig.STRIPE_SECRET_KEY

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $STRIPE_SECRET_KEY")
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(STRIPE_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val api: StripeAPI = retrofit.create(StripeAPI::class.java)
}

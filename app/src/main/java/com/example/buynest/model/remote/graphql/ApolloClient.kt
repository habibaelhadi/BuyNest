package com.example.buynest.model.remote.graphql

import com.apollographql.apollo3.ApolloClient
import com.example.buynest.BuildConfig

object ApolloClient {
    private const val BASE_URL = "https://mad45-alex-and03.myshopify.com/admin/api/2025-04/graphql.json"
    private const val ACCESS_TOKEN = BuildConfig.SHOPIFY_ACCESS_TOKEN

    val apolloClient: ApolloClient by lazy {
        ApolloClient.Builder()
            .serverUrl(BASE_URL)
            .addHttpHeader("X-Shopify-Access-Token", ACCESS_TOKEN)
            .addHttpHeader("Content-Type", "application/json")
            .build()
    }
}
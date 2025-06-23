package com.example.buynest.model.remote.graphql

import com.apollographql.apollo3.ApolloClient
import com.example.buynest.BuildConfig

object ApolloAdmin {
    private const val BASE_URL = "https://mad45-alex-and03.myshopify.com/admin/api/2025-01/graphql.json"
    private const val ACCESS_TOKEN_Admin = BuildConfig.Admin_ACCESS_TOKEN

    val apolloAdmin: ApolloClient by lazy {
        ApolloClient.Builder()
            .serverUrl(BASE_URL)
            .addHttpHeader("X-Shopify-Access-Token", ACCESS_TOKEN_Admin)
            .addHttpHeader("Content-Type", "application/json")
            .build()
    }
}
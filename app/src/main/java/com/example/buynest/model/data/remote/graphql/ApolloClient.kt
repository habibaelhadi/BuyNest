package com.example.buynest.model.data.remote.graphql

import com.apollographql.apollo3.ApolloClient

object ApolloClient {
    fun createApollo(
        BASE_URL: String,
        ACCESS_TOKEN: String,
        Header: String
    ): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(BASE_URL)
            .addHttpHeader(Header, ACCESS_TOKEN)
            .addHttpHeader("Content-Type", "application/json")
            .build()
    }
}
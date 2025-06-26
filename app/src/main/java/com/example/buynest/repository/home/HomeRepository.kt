package com.example.buynest.repository.home

import com.example.buynest.BrandsAndProductsQuery
import com.example.buynest.BuildConfig
import com.example.buynest.ProductsByCollectionIDQuery
import com.example.buynest.model.data.remote.graphql.ApolloClient
import com.example.buynest.utils.constant.CLIENT_BASE_URL
import com.example.buynest.utils.constant.CLIENT_HEADER
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class HomeRepository : IHomeRepository {
    override fun getBrands(): Flow<BrandsAndProductsQuery.Data?> = flow {
        val response = ApolloClient.createApollo(
            BASE_URL = CLIENT_BASE_URL,
            ACCESS_TOKEN = BuildConfig.SHOPIFY_ACCESS_TOKEN,
            Header = CLIENT_HEADER
        )
            .query(BrandsAndProductsQuery())
            .execute()

        emit(response.data)
    }.catch {
        emit(null)
    }

    override fun getBrandProducts(id: String): Flow<ProductsByCollectionIDQuery.Data?>  = flow {
        val response = ApolloClient.createApollo(
            BASE_URL = CLIENT_BASE_URL,
            ACCESS_TOKEN = BuildConfig.SHOPIFY_ACCESS_TOKEN,
            Header = CLIENT_HEADER
        )
            .query(ProductsByCollectionIDQuery(id))
            .execute()
        emit(response.data)
    }.catch {
        emit(null)
    }
}
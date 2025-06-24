package com.example.buynest.repository.productDetails

import com.example.buynest.BuildConfig
import com.example.buynest.ProductDetailsByIDQuery
import com.example.buynest.model.data.remote.graphql.ApolloClient
import com.example.buynest.utils.constant.CLIENT_BASE_URL
import com.example.buynest.utils.constant.CLIENT_HEADER
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class ProductDetailsRepositoryImpl: ProductDetailsRepository {
    override fun getProductDetails(id: String): Flow<ProductDetailsByIDQuery.Data?> = flow {
        val response = ApolloClient.createApollo(
            BASE_URL = CLIENT_BASE_URL,
            ACCESS_TOKEN = BuildConfig.SHOPIFY_ACCESS_TOKEN,
            Header = CLIENT_HEADER
        )
            .query(ProductDetailsByIDQuery(id))
            .execute()

        emit(response.data)
    }.catch {
        emit(null)
    }
}
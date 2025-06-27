package com.example.buynest.repository.productDetails

import com.apollographql.apollo3.api.ApolloResponse
import com.example.buynest.BuildConfig
import com.example.buynest.ProductDetailsByIDQuery
import com.example.buynest.model.data.remote.graphql.ApolloClient
import com.example.buynest.utils.constant.CLIENT_BASE_URL
import com.example.buynest.utils.constant.CLIENT_HEADER
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class ProductDetailsRepositoryImpl(
    private val apolloClient: com.apollographql.apollo3.ApolloClient
) : ProductDetailsRepository {
    override fun getProductDetails(id: String): Flow<ProductDetailsByIDQuery.Data?> = flow {
        val response = apolloClient.query(ProductDetailsByIDQuery(id)).execute()
        emit(response.data)
    }.catch {
        emit(null)
    }
}



package com.example.buynest.model.repository.productDetails

import com.example.buynest.ProductDetailsByIDQuery
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



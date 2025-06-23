package com.example.buynest.repository.productDetails

import com.example.buynest.ProductDetailsByIDQuery
import com.example.buynest.model.data.remote.graphql.ApolloClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class ProductDetailsRepositoryImpl: ProductDetailsRepository {
    override fun getProductDetails(id: String): Flow<ProductDetailsByIDQuery.Data?> = flow {
        val response = ApolloClient.apolloClient
            .query(ProductDetailsByIDQuery(id))
            .execute()

        emit(response.data)
    }.catch {
        emit(null)
    }
}
package com.example.buynest.repository.home

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.example.buynest.BrandsAndProductsQuery
import com.example.buynest.ProductsByCollectionIDQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class HomeRepository(private val response: ApolloClient) : IHomeRepository {
    override fun getBrands(): Flow<BrandsAndProductsQuery.Data?> = flow {
        val result: ApolloResponse<BrandsAndProductsQuery.Data> =
            response.query(BrandsAndProductsQuery()).execute()

        emit(result.data)

    }.catch {
        emit(null)
    }

    override fun getBrandProducts(id: String): Flow<ProductsByCollectionIDQuery.Data?> = flow {
        val result = response.query(ProductsByCollectionIDQuery(id)).execute()
        emit(result.data)
    }.catch {
        emit(null)
    }
}

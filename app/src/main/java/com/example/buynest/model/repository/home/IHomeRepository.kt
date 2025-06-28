package com.example.buynest.model.repository.home

import com.example.buynest.BrandsAndProductsQuery
import com.example.buynest.ProductsByCollectionIDQuery
import kotlinx.coroutines.flow.Flow

interface IHomeRepository {
    fun getBrands():  Flow<BrandsAndProductsQuery.Data?>
    fun getBrandProducts(id: String): Flow<ProductsByCollectionIDQuery.Data?>
}
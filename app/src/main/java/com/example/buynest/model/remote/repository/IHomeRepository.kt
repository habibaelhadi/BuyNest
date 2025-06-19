package com.example.buynest.model.remote.repository

import com.example.buynest.BrandsAndProductsQuery
import kotlinx.coroutines.flow.Flow

interface IHomeRepository {
    fun getBrands():  Flow<BrandsAndProductsQuery.Data?>
    fun getBrandProducts(): Flow<BrandsAndProductsQuery.Data?>
}
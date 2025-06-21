package com.example.buynest.repository.productDetails

import com.example.buynest.ProductDetailsByIDQuery
import kotlinx.coroutines.flow.Flow

interface ProductDetailsRepository {
    fun getProductDetails(id: String): Flow<ProductDetailsByIDQuery.Data?>
}
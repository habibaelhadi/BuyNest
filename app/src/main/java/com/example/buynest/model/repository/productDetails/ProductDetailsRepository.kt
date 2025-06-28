package com.example.buynest.model.repository.productDetails

import com.example.buynest.ProductDetailsByIDQuery
import kotlinx.coroutines.flow.Flow

interface ProductDetailsRepository {
    fun getProductDetails(id: String): Flow<ProductDetailsByIDQuery.Data?>
}
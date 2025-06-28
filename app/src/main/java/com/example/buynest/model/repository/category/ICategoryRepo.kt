package com.example.buynest.model.repository.category

import com.example.buynest.ProductsByHandleQuery
import kotlinx.coroutines.flow.Flow

interface ICategoryRepo {
    fun getProductByCategoryName(categoryName:String) : Flow<ProductsByHandleQuery.Data?>
}
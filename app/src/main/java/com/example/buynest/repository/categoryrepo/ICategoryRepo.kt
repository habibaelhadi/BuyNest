package com.example.buynest.repository.categoryrepo

import com.example.buynest.ProductsByHandleQuery
import kotlinx.coroutines.flow.Flow

interface ICategoryRepo {
    fun getProductByCategoryName(categoryName:String) : Flow<ProductsByHandleQuery.Data?>
}
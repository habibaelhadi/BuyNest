package com.example.buynest.model.repository.favorite

import com.example.buynest.ProductsDetailsByIDsQuery
import com.example.buynest.model.state.FirebaseResponse
import kotlinx.coroutines.flow.Flow

interface FavoriteRepo {
    suspend fun addToFavorite(productId: String): Result<Unit>
    suspend fun removeFromFavorite(productId: String): Result<Unit>
    suspend fun getAllFavorites(): Result<List<String>>
    fun getProductsByIds(productId: List<String>): Flow<ProductsDetailsByIDsQuery.Data?>
    fun setFirebaseResponse(response: FirebaseResponse)
}
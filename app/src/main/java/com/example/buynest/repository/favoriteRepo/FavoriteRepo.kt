package com.example.buynest.repository.favoriteRepo

interface FavoriteRepo {
    suspend fun addToFavorite(productId: String): Result<Unit>
    suspend fun removeFromFavorite(productId: String): Result<Unit>
    suspend fun getAllFavorites(): Result<List<String>>
}
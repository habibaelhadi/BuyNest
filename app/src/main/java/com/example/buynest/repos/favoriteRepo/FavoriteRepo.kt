package com.example.buynest.repos.favoriteRepo

interface FavoriteRepo {
    suspend fun addToFavorite(productId: String): Result<Unit>
    suspend fun removeFromFavorite(productId: String): Result<Unit>
    suspend fun isFavorite(productId: String): Result<Boolean>
    suspend fun getAllFavorites(): Result<List<String>>
}
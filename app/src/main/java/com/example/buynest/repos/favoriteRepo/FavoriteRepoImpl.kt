package com.example.buynest.repos.favoriteRepo

import com.example.buynest.repos.authenticationrepo.firebase.FirebaseResponse
import com.example.buynest.repos.favoriteRepo.favFirebase.FavFirebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FavoriteRepoImpl: FavoriteRepo {
    override suspend fun addToFavorite(productId: String): Result<Unit> =
        suspendCoroutine { continuation ->
            FavFirebase.setFirebaseResponse(object : FirebaseResponse {
                override fun <T> onResponseSuccess(message: T) {
                    continuation.resume(Result.success(Unit))
                }

                override fun <T> onResponseFailure(message: T) {
                    continuation.resume(Result.failure(Exception(message.toString())))
                }
            })

            FavFirebase.addToFavorite(productId)
        }


    override suspend fun removeFromFavorite(productId: String): Result<Unit> =
        suspendCoroutine { continuation ->
            FavFirebase.setFirebaseResponse(object : FirebaseResponse {
                override fun <T> onResponseSuccess(message: T) {
                    continuation.resume(Result.success(Unit))
                }

                override fun <T> onResponseFailure(message: T) {
                    continuation.resume(Result.failure(Exception(message.toString())))
                }
            })

            FavFirebase.removeFromFavorite(productId)
        }


    override suspend fun isFavorite(productId: String): Result<Boolean> =
        suspendCoroutine { continuation ->
            FavFirebase.setFirebaseResponse(object : FirebaseResponse {
                override fun <T> onResponseSuccess(message: T) {
                    val isFav = (message as? Boolean) ?: false
                    continuation.resume(Result.success(isFav))
                }

                override fun <T> onResponseFailure(message: T) {
                    continuation.resume(Result.failure(Exception(message.toString())))
                }
            })
            FavFirebase.isFavorite(productId)
        }

    override suspend fun getAllFavorites(): Result<List<String>> =
        suspendCoroutine { continuation ->
            FavFirebase.setFirebaseResponse(object : FirebaseResponse {
                override fun <T> onResponseSuccess(message: T) {
                    val favorites = (message as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                    continuation.resume(Result.success(favorites))
                }
                override fun <T> onResponseFailure(message: T) {
                    continuation.resume(Result.failure(Exception(message.toString())))
                }
            })
            FavFirebase.getAllFavorites()
        }
}
package com.example.buynest.repository.favoriteRepo

import com.example.buynest.repository.authenticationrepo.firebase.FirebaseResponse
import com.example.buynest.repository.favoriteRepo.favFirebase.FavFirebase
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


    override suspend fun getAllFavorites(): Result<List<String>> =
        suspendCoroutine { continuation ->
            var isResumed = false

            val safeResume: (Result<List<String>>) -> Unit = { result ->
                if (!isResumed) {
                    isResumed = true
                    continuation.resume(result)
                }
            }
            FavFirebase.setFirebaseResponse(object : FirebaseResponse {
                override fun <T> onResponseSuccess(message: T) {
                    val favorites = (message as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                    safeResume(Result.success(favorites))
                }
                override fun <T> onResponseFailure(message: T) {
                    safeResume(Result.failure(Exception(message.toString())))
                }
            })
            FavFirebase.getAllFavorites()
        }
}
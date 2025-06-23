package com.example.buynest.repository.favorite


import com.example.buynest.ProductsDetailsByIDsQuery
import com.example.buynest.model.data.remote.graphql.ApolloClient
import com.example.buynest.model.state.FirebaseResponse
import com.example.buynest.repository.favorite.favFirebase.FavFirebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FavoriteRepoImpl: FavoriteRepo {
    override suspend fun addToFavorite(productId: String): Result<Unit> =
        suspendCoroutine { continuation ->
            var isResumed = false

            val safeResume: (Result<Unit>) -> Unit = { result ->
                if (!isResumed) {
                    isResumed = true
                    continuation.resume(result)
                }
            }
            FavFirebase.setFirebaseResponse(object : FirebaseResponse {
                override fun <T> onResponseSuccess(message: T) {
                    safeResume(Result.success(Unit))
                }

                override fun <T> onResponseFailure(message: T) {
                    safeResume(Result.failure(Exception(message.toString())))
                }
            })

            FavFirebase.addToFavorite(productId)
        }


    override suspend fun removeFromFavorite(productId: String): Result<Unit> =
        suspendCoroutine { continuation ->
            var isResumed = false

            val safeResume: (Result<Unit>) -> Unit = { result ->
                if (!isResumed) {
                    isResumed = true
                    continuation.resume(result)
                }
            }
            FavFirebase.setFirebaseResponse(object : FirebaseResponse {
                override fun <T> onResponseSuccess(message: T) {
                    safeResume(Result.success(Unit))
                }

                override fun <T> onResponseFailure(message: T) {
                    safeResume(Result.failure(Exception(message.toString())))
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

   override fun getProductsByIds(productId: List<String>): Flow<ProductsDetailsByIDsQuery.Data?> = flow {
        val response = ApolloClient.apolloClient
            .query(ProductsDetailsByIDsQuery(productId))
            .execute()
        emit(response.data)
    }.catch {
        emit(null)
    }
}
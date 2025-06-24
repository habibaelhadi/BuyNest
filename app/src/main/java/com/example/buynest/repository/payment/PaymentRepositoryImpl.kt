package com.example.buynest.repository.payment

import com.example.buynest.model.data.remote.rest.IRemoteDataSource
import com.google.gson.JsonObject
import retrofit2.Response

class PaymentRepositoryImpl(private val remoteDataSource: IRemoteDataSource) : IPaymentRepository {

    override  suspend fun createPaymentIntent(
        amount: Double,
        currency: String,
        paymentMethod: String
    ): Response<JsonObject> {
        return remoteDataSource.createPaymentIntent(amount, currency)
    }
}

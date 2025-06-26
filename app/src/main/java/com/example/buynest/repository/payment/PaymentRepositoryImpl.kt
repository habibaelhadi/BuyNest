package com.example.buynest.repository.payment

import com.example.buynest.repository.payment.datasource.IPaymentDataSource
import com.google.gson.JsonObject
import retrofit2.Response

class PaymentRepositoryImpl(private val remoteDataSource: IPaymentDataSource) : IPaymentRepository {

    override  suspend fun createPaymentIntent(
        amount: Int,
        currency: String,
        paymentMethod: String
    ): Response<JsonObject> {
        return remoteDataSource.createPaymentIntent(amount, currency)
    }
}

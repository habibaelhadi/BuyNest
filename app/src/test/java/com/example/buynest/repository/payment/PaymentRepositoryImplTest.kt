package com.example.buynest.repository.payment

import org.junit.Assert.*
import com.example.buynest.repository.payment.datasource.IPaymentDataSource
import com.google.gson.JsonObject
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class PaymentRepositoryImplTest {

    private lateinit var dataSource: IPaymentDataSource
    private lateinit var repository: PaymentRepositoryImpl

    @Before
    fun setup() {
        dataSource = mockk()
        repository = PaymentRepositoryImpl(dataSource)
    }

    @Test
    fun `createPaymentIntent should return response from dataSource`() = runTest {
        // Arrange
        val amount = 1000
        val currency = "usd"
        val paymentMethod = "card"
        val expectedJson = JsonObject().apply {
            addProperty("client_secret", "test_secret")
        }
        val expectedResponse = Response.success(expectedJson)

        coEvery { dataSource.createPaymentIntent(amount, currency) } returns expectedResponse

        // Act
        val result = repository.createPaymentIntent(amount, currency, paymentMethod)

        // Assert
        assertEquals(expectedResponse, result)
        assertEquals("test_secret", result.body()?.get("client_secret")?.asString)

        coVerify { dataSource.createPaymentIntent(amount, currency) }
    }
}

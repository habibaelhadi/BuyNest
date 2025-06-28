package com.example.buynest.viewmodel.payment

import com.example.buynest.model.repository.payment.IPaymentRepository
import com.google.gson.JsonObject
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.junit.*
import retrofit2.Response
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: IPaymentRepository
    private lateinit var viewModel: PaymentViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = PaymentViewModel(repository)
        mockkStatic("android.util.Log")
        every { android.util.Log.i(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initiatePaymentFlow should emit clientSecret on success`() = runTest {
        val fakeSecret = "test_secret_123"
        val json = JsonObject().apply {
            addProperty("client_secret", fakeSecret)
        }

        coEvery { repository.createPaymentIntent(any(), any()) } returns Response.success(json)

        var callbackResult: String? = null
        viewModel.initiatePaymentFlow(amount = 100) { secret ->
            callbackResult = secret
        }

        advanceUntilIdle()

        assertEquals(null, viewModel.error.value)

        assertEquals(fakeSecret, callbackResult)
    }

    @Test
    fun `initiatePaymentFlow should emit error if client_secret is missing`() = runTest {
        val json = JsonObject()

        coEvery { repository.createPaymentIntent(any(), any()) } returns Response.success(json)

        viewModel.initiatePaymentFlow(amount = 100) { }

        advanceUntilIdle()

        assertEquals("Missing client_secret in response", viewModel.error.value)
    }

    @Test
    fun `initiatePaymentFlow should emit error on failed response`() = runTest {
        val errorBody = ResponseBody.create(
            "application/json".toMediaTypeOrNull(),
            "Internal Server Error"
        )

        coEvery { repository.createPaymentIntent(any(), any()) } returns Response.error(500, errorBody)

        viewModel.initiatePaymentFlow(amount = 100) { }

        advanceUntilIdle()

        assertEquals("Error: Internal Server Error", viewModel.error.value)
    }

    @Test
    fun `initiatePaymentFlow should emit exception message`() = runTest {
        coEvery { repository.createPaymentIntent(any(), any()) } throws RuntimeException("Network down")

        viewModel.initiatePaymentFlow(amount = 100) { }

        advanceUntilIdle()

        assertEquals("Exception: Network down", viewModel.error.value)
    }
}

package com.example.buynest.viewmodel.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buynest.repository.payment.IPaymentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PaymentViewModel(
    private val repository: IPaymentRepository
) : ViewModel() {

    private val _clientSecret = MutableStateFlow<String?>(null)

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun initiatePaymentFlow(
        amount: Double,
        currency: String = "usd",
        onClientSecretReady: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.createPaymentIntent(amount, currency)
                if (response.isSuccessful) {
                    val secret = response.body()?.get("client_secret")?.asString
                    if (secret != null) {
                        _clientSecret.value = secret
                        onClientSecretReady(secret)
                    } else {
                        _error.value = "Missing client_secret in response"
                    }
                } else {
                    _error.value = "Error: ${response.errorBody()?.string() ?: "Unknown error"}"
                }
            } catch (e: Exception) {
                _error.value = "Exception: ${e.localizedMessage ?: "Unknown exception"}"
            }
        }
    }
}

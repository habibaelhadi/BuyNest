package com.example.buynest.viewmodel.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.buynest.repository.payment.IPaymentRepository

class PaymentViewModelFactory(
    private val repository: IPaymentRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PaymentViewModel(repository) as T
    }
}
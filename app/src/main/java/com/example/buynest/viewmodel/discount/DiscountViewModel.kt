package com.example.buynest.viewmodel.discount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buynest.model.entity.OfferModel
import com.example.buynest.repository.discount.DiscountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DiscountViewModel(private val repository: DiscountRepository) : ViewModel() {

    private val _offers = MutableStateFlow<List<OfferModel>>(emptyList())
    val offers: StateFlow<List<OfferModel>> = _offers

    fun loadDiscounts() {
        viewModelScope.launch {
            _offers.value = repository.getAllDiscounts()
        }
    }

    suspend fun isCouponValid(coupon: String): Boolean {
        return repository.isCouponValid(coupon)
    }

    suspend fun applyCoupon(coupon: String): Double {
        return repository.getDiscountAmount(coupon)
    }

}

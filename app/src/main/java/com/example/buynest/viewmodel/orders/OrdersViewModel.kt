package com.example.buynest.viewmodel.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.repository.home.IHomeRepository
import com.example.buynest.repository.order.IOrderRepo
import com.example.buynest.viewmodel.home.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class OrdersViewModel(private val repository: IOrderRepo) : ViewModel() {
    private val _mutableOrders = MutableStateFlow<UiResponseState>(UiResponseState.Loading)
    val orders = _mutableOrders

    fun getAllOrders(email: String){
        viewModelScope.launch {
            repository.getAllOrders(email).collect {
                try {
                    _mutableOrders.value = UiResponseState.Loading
                    if (true) {
                        _mutableOrders.value = UiResponseState.Success(it)
                        } else {
                        _mutableOrders.value = UiResponseState.Error("No data received.")
                    }
                } catch (e: Exception) {
                    _mutableOrders.value = UiResponseState.Error(e.message.toString())
                }
            }
        }
    }
}


class OrdersFactory(private val repo: IOrderRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrdersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrdersViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
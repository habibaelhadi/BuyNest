package com.example.buynest.viewmodel.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buynest.admin.GetOrdersByEmailQuery
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.repository.home.IHomeRepository
import com.example.buynest.repository.order.IOrderRepo
import com.example.buynest.viewmodel.home.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class OrdersViewModel(private val repository: IOrderRepo) : ViewModel() {
    private val _mutableOrders = MutableStateFlow<UiResponseState>(UiResponseState.Loading)
    val orders = _mutableOrders
    val selectedOrder = MutableStateFlow<GetOrdersByEmailQuery.Node?>(null)

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

    fun setSelectedOrder(order: GetOrdersByEmailQuery.Node) {
        selectedOrder.value = order
    }

    fun extractImageUrlsFromNote(note: String?): List<String> {
        if (note == null) return emptyList()
        return note.lines()
            .filter { it.contains("Image: ") }
            .mapNotNull {
                val parts = it.split("Image: ")
                if (parts.size == 2) parts[1].trim() else null
            }
    }
    fun extractPaymentMethodFromNote(note: String?): String {
        if (note == null) return "Unknown"

        val lines = note.lines()
        for (line in lines) {
            if (line.startsWith("PaymentStatus:")) {
                return line.substringAfter("Payment Method:").trim()
            }
        }
        return "Unknown"
    }

}
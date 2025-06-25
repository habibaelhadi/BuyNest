package com.example.buynest.viewmodel.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buynest.admin.GetOrdersByEmailQuery
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.repository.order.IOrderRepo
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

    fun extractPriceDetailsFromNote(note: String?): Triple<Int, Int, Int> {
        if (note.isNullOrEmpty()) return Triple(0, 0, 0)

        val totalBefore = Regex("""TotalBefore:\s*(\d+)""")
            .find(note)?.groupValues?.get(1)?.toIntOrNull() ?: 0

        val discount = Regex("""Discount:\s*(\d+)""")
            .find(note)?.groupValues?.get(1)?.toIntOrNull() ?: 0

        val totalAfter = Regex("""TotalAfter:\s*(\d+)""")
            .find(note)?.groupValues?.get(1)?.toIntOrNull() ?: 0

        return Triple(totalBefore, discount, totalAfter)
    }


}
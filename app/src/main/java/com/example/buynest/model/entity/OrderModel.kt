package com.example.buynest.model.entity

data class OrderModel(
    val email: String,
    val address: AddressModel,
    val orderItems: List<CartItem>,
    val isPaid: Boolean = false,
    val discount: Double = 0.0
)


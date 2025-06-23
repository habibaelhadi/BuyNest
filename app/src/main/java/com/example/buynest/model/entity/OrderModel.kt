package com.example.buynest.model.entity

data class OrderModel(
    val email: String,
    val address: AddressModel,
    val orderItems: List<CartItem>
)


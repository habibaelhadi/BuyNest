package com.example.buynest.model.entity

data class OrderModel(
    val email: String,
    val address: AddressModel,
    val orderItems: List<CartItem>
)

//data class OrderItem(
//    val variantId: String,
//    val quantity: Int,
//    val color: String,
//    val size: Int,
//    val price: Int,
//    val imageUrl: String,
//    val name: String
//)

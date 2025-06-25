package com.example.buynest.model.entity

data class CartItem(
    val id: Int,
    val lineId: String,
    val name: String,
    val price: Int,
    val color: String,
    val size: Int,
    val imageUrl: String,
    val quantity: Int,
    val variantId: String = "",
    val currencySymbol: String = ""
)
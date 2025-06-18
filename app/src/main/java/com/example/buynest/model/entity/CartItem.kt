package com.example.buynest.model.entity

data class CartItem(
    val id: Int,
    val name: String,
    val price: Int,
    val color: String,
    val size: Int,
    val imageRes: Int,
    val quantity: Int
)
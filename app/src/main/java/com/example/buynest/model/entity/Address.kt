package com.example.buynest.model.entity
import androidx.compose.ui.graphics.vector.ImageVector

data class Address(
    val label: String,
    val icon: ImageVector,
    val address: String,
    val phone: String
)
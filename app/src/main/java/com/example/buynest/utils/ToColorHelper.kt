package com.example.buynest.utils

import androidx.compose.ui.graphics.Color

fun List<String>.toColorList(): List<Color> {
    return this.mapNotNull { colorName ->
        when (colorName.lowercase()) {
            "red" -> Color.Red
            "blue" -> Color.Blue
            "green" -> Color.Green
            "black" -> Color.Black
            "orange" -> Color(0xFFFF9800)
            "white" -> Color.White
            else -> null
        }
    }
}

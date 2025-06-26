package com.example.buynest.model.mapper

import androidx.compose.ui.graphics.Color

fun List<String>.toColorList(): List<Color> {
    return this.map { colorName ->
        when (colorName.lowercase()) {
            "red" -> Color.Red
            "green" -> Color.Green
            "blue" -> Color.Blue
            "yellow" -> Color.Yellow
            "black" -> Color.Black
            "white" -> Color.White
            "gray", "grey" -> Color.Gray
            "purple" -> Color(0xFF800080)
            "pink" -> Color(0xFFFFC0CB)
            "orange" -> Color(0xFFFFA500)
            "brown" -> Color(0xFFA52A2A)
            else -> Color.LightGray
        }
    }
}

fun mapColorNameToColor(name: String?): Color {
    return when (name?.lowercase()) {
        "red" -> Color.Red
        "green" -> Color.Green
        "blue" -> Color.Blue
        "yellow" -> Color.Yellow
        "black" -> Color.Black
        "white" -> Color.White
        "gray", "grey" -> Color.Gray
        "purple" -> Color(0xFF800080)
        "pink" -> Color(0xFFFFC0CB)
        "orange" -> Color(0xFFFFA500)
        "brown" -> Color(0xFFA52A2A)
        else -> Color.LightGray // fallback
    }
}
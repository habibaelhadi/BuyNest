package com.example.buynest.model.mapper

import androidx.compose.ui.graphics.Color
import com.example.buynest.ui.theme.Gray
import com.example.buynest.ui.theme.LightGray2
import com.example.buynest.ui.theme.Yellow
import com.example.buynest.ui.theme.beige
import com.example.buynest.ui.theme.black
import com.example.buynest.ui.theme.blue
import com.example.buynest.ui.theme.brown
import com.example.buynest.ui.theme.burgundy
import com.example.buynest.ui.theme.green
import com.example.buynest.ui.theme.lightBrown
import com.example.buynest.ui.theme.orange
import com.example.buynest.ui.theme.pink
import com.example.buynest.ui.theme.purple
import com.example.buynest.ui.theme.red
import com.example.buynest.ui.theme.white

fun List<String>.toColorList(): List<Color> {
    return this.map { colorName ->
        when (colorName.lowercase()) {
            "red" -> red
            "green" -> green
            "blue" -> blue
            "yellow" -> Yellow
            "black" -> black
            "white" -> white
            "gray", "grey" -> Gray
            "purple" -> purple
            "pink" -> pink
            "orange" -> orange
            "brown" -> brown
            "burgundy", "burgandy"  -> burgundy
            "beige" -> beige
            "light_brown" -> lightBrown
            else -> LightGray2
        }
    }
}

fun mapColorNameToColor(name: String?): Color {
    return when (name?.lowercase()) {
        "red" -> red
        "green" -> green
        "blue" -> blue
        "yellow" -> Yellow
        "black" -> black
        "white" -> white
        "gray", "grey" -> Gray
        "purple" -> purple
        "pink" -> pink
        "orange" -> orange
        "brown" -> brown
        "burgundy", "burgandy" -> burgundy
        "beige" -> beige
        "light_brown" -> lightBrown
        else -> LightGray2
    }
}
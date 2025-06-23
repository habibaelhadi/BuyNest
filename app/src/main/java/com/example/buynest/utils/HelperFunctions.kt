package com.example.buynest.utils

import androidx.compose.ui.graphics.Color
import com.example.buynest.BrandsAndProductsQuery
import com.example.buynest.ProductsByHandleQuery
import com.example.buynest.model.entity.UiProduct

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

fun mapFromBrandProduct(node: BrandsAndProductsQuery.Node): UiProduct {
    return UiProduct(
        id = node.id,
        title = node.title,
        imageUrl = node.featuredImage?.url ?: ""
    )
}

fun mapFromCategoryProduct(node: ProductsByHandleQuery.Node): UiProduct {
    return UiProduct(
        id = node.id,
        title = node.title,
        imageUrl = node.featuredImage?.url ?: ""
    )
}
package com.example.buynest.model.mapper

fun mapSizeFromTextToInteger(size: String): Int{
    return when(size.uppercase()){
        "OS" -> 0
        "XS"  -> 0
        "S" -> 1
        "M" -> 2
        "L" -> 3
        "XL" -> 4
        "XXL" -> 5
        else -> size.toIntOrNull() ?: -1
    }
}


fun mapSizeFromIntegerToText(size: Int): String{
    return when(size){
        0 -> "XS"
        1 -> "S"
        2 -> "M"
        3 -> "L"
        4 ->"XL"
        5 -> "XXL"
        else -> " "
    }
}
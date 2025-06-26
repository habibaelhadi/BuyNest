package com.example.buynest.utils

import androidx.compose.ui.graphics.Color
import com.example.buynest.BrandsAndProductsQuery
import com.example.buynest.ProductsByHandleQuery
import com.example.buynest.model.entity.UiProduct
import com.example.buynest.model.mapper.countryToCurrencyCodeMap
import com.example.buynest.model.mapper.currencySymbolMap

fun mapFromBrandProduct(node: BrandsAndProductsQuery.Node): UiProduct {
    return UiProduct(
        id = node.id,
        title = node.title,
        imageUrl = node.featuredImage?.url ?: "",
        price = node.variants.edges.firstOrNull()?.node?.price?.amount?.toString()?.toFloatOrNull()
    )
}

fun mapFromCategoryProduct(node: ProductsByHandleQuery.Node): UiProduct {
    return UiProduct(
        id = node.id,
        title = node.title,
        imageUrl = node.featuredImage?.url ?: "",
        price = node.variants.edges.firstOrNull()?.node?.price?.amount?.toString()?.toFloatOrNull()
    )
}

fun getCurrencySymbol(code: String): String {
    return currencySymbolMap[code] ?: code
}

fun getCurrencyName(code: String): String {
    return countryToCurrencyCodeMap[code] ?: code
}



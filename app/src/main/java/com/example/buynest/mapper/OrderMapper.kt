package com.example.buynest.mapper

import com.example.buynest.model.entity.OrderModel

fun OrderModel.toGraphQLVariables(): Map<String, Any?> {
    val addressInput = mapOf(
        "firstName" to address.firstName,
        "lastName" to address.lastName,
        "address1" to address.address1,
        "city" to address.city,
        "country" to address.country,
    )

    val lineItemsInput = orderItems.map {
        mapOf(
            "variantId" to "gid://shopify/ProductVariant/${it.id}",
            "quantity" to it.quantity
        )
    }

    val totalBefore = orderItems.sumOf { it.price * it.quantity }
    val discount = 100
    val totalAfter = totalBefore - discount

    val note = "TotalBefore: $totalBefore EGP | Discount: $discount EGP | TotalAfter: $totalAfter EGP"

    return mapOf(
        "email" to email,
        "shippingAddress" to addressInput,
        "lineItems" to lineItemsInput,
        "note" to note
    )
}

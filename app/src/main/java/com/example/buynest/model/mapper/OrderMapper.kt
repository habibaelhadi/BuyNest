package com.example.buynest.model.mapper

import com.apollographql.apollo3.api.Optional
import com.example.buynest.admin.type.AttributeInput
import com.example.buynest.admin.type.DraftOrderInput
import com.example.buynest.admin.type.DraftOrderLineItemInput
import com.example.buynest.admin.type.MailingAddressInput
import com.example.buynest.model.entity.OrderModel

fun OrderModel.toDraftOrderInput(): DraftOrderInput {
    val addressInput = MailingAddressInput(
        firstName = Optional.presentIfNotNull(address.firstName),
        address1 = Optional.presentIfNotNull(address.address1),
        address2 = Optional.presentIfNotNull(address.address2),
        city = Optional.presentIfNotNull(address.city),
        country = Optional.presentIfNotNull(address.country),
        phone = Optional.presentIfNotNull(address.phone),
    )

    val lineItems = orderItems.map { item ->
        DraftOrderLineItemInput(
            quantity = item.quantity,
            variantId = Optional.Present(item.variantId),
        )
    }

    val totalBefore = orderItems.sumOf { it.price * it.quantity }
    val discountAmount = (totalBefore * discount).toInt()
    val totalAfter = totalBefore - discountAmount
    val discountPercentage = (discount * 100).toInt()

    val status = if (isPaid) "PAID" else "UNPAID"

    val noteBuilder = StringBuilder()
    noteBuilder.append("PaymentStatus: $status\n")
    noteBuilder.append("TotalBefore: $totalBefore EGP\n")
    noteBuilder.append("Discount: $discountAmount EGP ($discountPercentage%)\n")
    noteBuilder.append("TotalAfter: $totalAfter EGP\n")
    orderItems.forEachIndexed { index, item ->
        noteBuilder.append("Item ${index + 1}: ${item.name}, Image: ${item.imageUrl}\n")
    }

    val note = noteBuilder.toString().trim()

    // Optional: Custom attributes (visible in Shopify Admin, not in email by default)
    val attributes = listOf(
        AttributeInput("TotalBefore", totalBefore.toString()),
        AttributeInput("Discount", "$discountAmount EGP ($discountPercentage%)"),
        AttributeInput("TotalAfter", totalAfter.toString()),
        AttributeInput("PaymentStatus", status)
    )

    return DraftOrderInput(
        email = Optional.Present(email),
        shippingAddress = Optional.Present(addressInput),
        lineItems = Optional.Present(lineItems),
        note = Optional.Present(note),
        customAttributes = Optional.Present(attributes)
    )
}

package com.example.buynest.mapper

import com.apollographql.apollo3.api.Optional
import com.example.buynest.admin.type.DraftOrderInput
import com.example.buynest.admin.type.DraftOrderLineItemInput
import com.example.buynest.admin.type.MailingAddressInput
import com.example.buynest.model.entity.OrderModel

fun OrderModel.toDraftOrderInput(): DraftOrderInput {
    val addressInput = MailingAddressInput(
        firstName = Optional.presentIfNotNull(address.firstName),
        lastName = Optional.presentIfNotNull(address.lastName),
        address1 = Optional.presentIfNotNull(address.address1),
        address2 = Optional.presentIfNotNull(address.address2),
        city = Optional.presentIfNotNull(address.city),
        country = Optional.presentIfNotNull(address.country),
        phone = Optional.presentIfNotNull(address.phone),
        province = Optional.Absent,
        zip = Optional.Absent,
        company = Optional.Absent
    )

    val lineItemsInput = orderItems.map {
        DraftOrderLineItemInput(
            variantId = Optional.Present("gid://shopify/ProductVariant/${it.id}"),
            quantity = it.quantity
        )
    }

    val totalBefore = orderItems.sumOf { it.price * it.quantity }
    val discount = 100
    val totalAfter = totalBefore - discount
    val note = "TotalBefore: $totalBefore EGP | Discount: $discount EGP | TotalAfter: $totalAfter EGP"

    return DraftOrderInput(
        email = Optional.Present(email),
        shippingAddress = Optional.Present(addressInput),
        lineItems = Optional.Present(lineItemsInput),
        note = Optional.Present(note),
        tags = Optional.presentIfNotNull(listOf("Stripe Draft")),
        customAttributes = Optional.Absent,
        metafields = Optional.Absent,
        taxExempt = Optional.Absent,
        useCustomerDefaultAddress = Optional.Absent
    )
}


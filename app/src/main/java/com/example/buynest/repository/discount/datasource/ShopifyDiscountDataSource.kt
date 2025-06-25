package com.example.buynest.repository.discount.datasource

import com.example.buynest.model.entity.OfferModel

interface ShopifyDiscountDataSource {
    suspend fun fetchDiscounts(): List<OfferModel>
}
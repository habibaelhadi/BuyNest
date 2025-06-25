package com.example.buynest.repository.discount.datasource

import com.example.buynest.model.entity.OfferModel

interface ShopifyDiscountDataSource {
    suspend fun fetchDiscounts(): List<OfferModel>
    suspend fun isCouponValid(coupon: String): Boolean
    suspend fun getDiscountAmount(coupon: String): Double
}
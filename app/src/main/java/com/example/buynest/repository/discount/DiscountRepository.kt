package com.example.buynest.repository.discount

import com.example.buynest.model.entity.OfferModel

interface DiscountRepository {
    suspend fun getAllDiscounts(): List<OfferModel>
    suspend fun isCouponValid(coupon: String): Boolean
    suspend fun getDiscountAmount(coupon: String): Double
}
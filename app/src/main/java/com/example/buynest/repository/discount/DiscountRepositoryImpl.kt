package com.example.buynest.repository.discount

import com.example.buynest.model.entity.OfferModel
import com.example.buynest.repository.discount.datasource.ShopifyDiscountDataSource

class DiscountRepositoryImpl(private val dataSource: ShopifyDiscountDataSource) : DiscountRepository {

    override suspend fun getAllDiscounts(): List<OfferModel> = dataSource.fetchDiscounts()

    override suspend fun isCouponValid(coupon: String): Boolean = dataSource.isCouponValid(coupon)

    override suspend fun getDiscountAmount(coupon: String): Double = dataSource.getDiscountAmount(coupon)
}
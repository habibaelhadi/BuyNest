package com.example.buynest.model.repository.discount.datasource

import com.apollographql.apollo3.ApolloClient
import com.example.buynest.R
import com.example.buynest.admin.GetDiscountAmountDetailsQuery
import com.example.buynest.model.entity.OfferModel

class ShopifyDiscountDataSourceImpl(
    private val apolloClient: ApolloClient
) : ShopifyDiscountDataSource {
    override suspend fun fetchDiscounts(): List<OfferModel> {
        val response = apolloClient.query(GetDiscountAmountDetailsQuery()).execute()

        val discountEdges = response.data?.discountNodes?.edges.orEmpty()

        return discountEdges.mapIndexedNotNull {index, edge ->
            val node = edge?.node ?: return@mapIndexedNotNull null
            val discount = node.discount?.onDiscountCodeBasic ?: return@mapIndexedNotNull null
            val title = discount.title ?: return@mapIndexedNotNull null
            val percentage = discount.customerGets?.value?.onDiscountPercentage?.percentage ?: return@mapIndexedNotNull null

            val imageResId = when (index) {
                0 -> R.drawable.ad1_banner
                1 -> R.drawable.ad2_banner
                else -> R.drawable.default_banner
            }

            OfferModel(
                title = title,
                subtitle = "Get ${(percentage * 100).toInt()}% off",
                buttonText = "Apply Coupon",
                percentage = percentage,
                imageRes = imageResId
            )
        }
    }

    override suspend fun isCouponValid(coupon: String): Boolean {
        val response = apolloClient.query(GetDiscountAmountDetailsQuery()).execute()
        val discountEdges = response.data?.discountNodes?.edges.orEmpty()

        return discountEdges.any { edge ->
            val discount = edge?.node?.discount?.onDiscountCodeBasic
            val isTitleMatch = discount?.title?.trim()?.equals(coupon.trim(), ignoreCase = true) == true
            val isStatusActive = discount?.status?.name == "ACTIVE"
            isTitleMatch && isStatusActive
        }
    }

    override suspend fun getDiscountAmount(coupon: String): Double {
        val response = apolloClient.query(GetDiscountAmountDetailsQuery()).execute()
        val discountEdges = response.data?.discountNodes?.edges.orEmpty()

        val discountNode = discountEdges.firstOrNull { edge ->
            val discount = edge?.node?.discount?.onDiscountCodeBasic
            val isTitleMatch = discount?.title?.trim()?.equals(coupon.trim(), ignoreCase = true) == true
            val isStatusActive = discount?.status?.name == "ACTIVE"
            isTitleMatch && isStatusActive
        }?.node?.discount?.onDiscountCodeBasic

        return discountNode?.customerGets
            ?.value
            ?.onDiscountPercentage
            ?.percentage
            ?: 0.0
    }

}

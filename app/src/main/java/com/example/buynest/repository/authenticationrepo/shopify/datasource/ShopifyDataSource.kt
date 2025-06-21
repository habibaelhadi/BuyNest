package com.example.buynest.repository.authenticationrepo.shopify.datasource

interface ShopifyDataSource {
    fun createCustomerOnShopify(name: String, email: String, password: String)
    fun getCustomerAccessToken(email: String, password: String)
}
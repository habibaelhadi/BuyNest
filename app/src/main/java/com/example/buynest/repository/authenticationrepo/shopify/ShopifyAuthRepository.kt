package com.example.buynest.repository.authenticationrepo.shopify

import com.example.buynest.CreateCustomerMutation
import com.example.buynest.LoginCustomerMutation

interface ShopifyAuthRepository {
    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        phone: String?
    ): Result<CreateCustomerMutation.Customer?>

    suspend fun login(
        email: String,
        password: String
    ): Result<LoginCustomerMutation.CustomerAccessToken?>
}
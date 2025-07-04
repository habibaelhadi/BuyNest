package com.example.buynest.model.repository.authentication.shopify.datasource

import com.example.buynest.CreateCustomerMutation
import com.example.buynest.LoginCustomerMutation

interface ShopifyAuthRemoteDataSource {
    suspend fun signUpCustomer(
        firstName: String,
        email: String,
        password: String,
        phone: String?
    ): Result<CreateCustomerMutation.Customer?>

    suspend fun loginCustomer(
        email: String,
        password: String,
        cartId: String
    ): Result<LoginCustomerMutation.CustomerAccessToken?>
}
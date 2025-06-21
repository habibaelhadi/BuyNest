package com.example.buynest.repository.authenticationrepo.shopify.datasource

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
        password: String
    ): Result<LoginCustomerMutation.CustomerAccessToken?>
}
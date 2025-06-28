package com.example.buynest.model.repository.authentication.shopify

import com.example.buynest.CreateCustomerMutation
import com.example.buynest.LoginCustomerMutation
import com.example.buynest.model.repository.authentication.shopify.datasource.ShopifyAuthRemoteDataSource

class ShopifyAuthRepositoryImpl(
    private val remoteDataSource: ShopifyAuthRemoteDataSource
) : ShopifyAuthRepository {

    override suspend fun register(
        fullName: String,
        email: String,
        password: String,
        phone: String?
    ): Result<CreateCustomerMutation.Customer?> {
        return remoteDataSource.signUpCustomer(
            firstName = fullName,
            email = email,
            password = password,
            phone = phone
        )
    }

    override suspend fun login(
        email: String,
        password: String,
        cartId: String
    ): Result<LoginCustomerMutation.CustomerAccessToken?> {
        return remoteDataSource.loginCustomer(email, password,cartId)
    }
}
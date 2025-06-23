package com.example.buynest.repository.authentication.shopify.datasource

import android.util.Log
import com.apollographql.apollo3.api.Optional
import com.example.buynest.CreateCustomerMutation
import com.example.buynest.CustomerAccessTokenCreateMutation
import com.example.buynest.LoginCustomerMutation
import com.example.buynest.model.data.remote.graphql.ApolloClient.apolloClient
import com.example.buynest.repository.cart.CartRepositoryImpl
import com.example.buynest.repository.cart.datasource.CartDataSourceImpl
import com.example.buynest.type.CustomerAccessTokenCreateInput
import com.example.buynest.type.CustomerCreateInput
import com.example.buynest.utils.AppConstants.KEY_CART_ID
import com.example.buynest.utils.AppConstants.KEY_CHECKOUT_URL
import com.example.buynest.utils.AppConstants.KEY_CUSTOMER_ID
import com.example.buynest.utils.AppConstants.KEY_CUSTOMER_TOKEN
import com.example.buynest.utils.SecureSharedPrefHelper
import com.example.buynest.viewmodel.cart.CartManager

class ShopifyAuthRemoteDataSourceImpl: ShopifyAuthRemoteDataSource {

    override suspend fun signUpCustomer(
        firstName: String,
        email: String,
        password: String,
        phone: String?
    ): Result<CreateCustomerMutation.Customer?> {
        return try {
            val formattedPhone = phone?.takeIf { it.isNotBlank() }?.let {
                if (!it.startsWith("+")) "+2$it" else it
            }

            Log.i("TAG", "SignUp - Phone: $formattedPhone")
            Log.i("TAG", "SignUp - Name: $firstName")
            Log.i("TAG", "SignUp - Email: $email")

            val input = CustomerCreateInput(
                firstName = Optional.presentIfNotNull(firstName),
                email = email,
                password = password,
                phone = Optional.presentIfNotNull(formattedPhone)
            )

            val response = apolloClient
                .mutation(CreateCustomerMutation(input))
                .execute()

            val customer = response.data?.customerCreate?.customer
            val errors = response.data?.customerCreate?.userErrors.orEmpty()

            if (!errors.isNullOrEmpty() || customer == null) {
                val errorMessage = errors.firstOrNull()?.message ?: "Unknown error"
                Log.e("TAG", "SignUp failed: $errorMessage")
                return Result.failure(Exception(errorMessage))
            }

            // Save customer ID
            SecureSharedPrefHelper.putString(KEY_CUSTOMER_ID, customer.id)
            Log.i("TAG", "Saved Customer ID: ${customer.id}")

            // Get access token
            val tokenResponse = apolloClient.mutation(
                CustomerAccessTokenCreateMutation(
                    CustomerAccessTokenCreateInput(email, password)
                )
            ).execute()

            val accessToken = tokenResponse.data?.customerAccessTokenCreate?.customerAccessToken?.accessToken
            val tokenErrors = tokenResponse.data?.customerAccessTokenCreate?.userErrors.orEmpty()

            if (!tokenErrors.isNullOrEmpty() || accessToken == null) {
                val tokenErrorMessage = tokenErrors.firstOrNull()?.message ?: "Token creation failed"
                Log.e("TAG", "Access token error: $tokenErrorMessage")
                return Result.failure(Exception(tokenErrorMessage))
            }

            SecureSharedPrefHelper.putString(KEY_CUSTOMER_TOKEN, accessToken)
            Log.i("TAG", "Saved Customer Token: $accessToken")

            // Create cart
            CartManager.setup(CartRepositoryImpl(CartDataSourceImpl(apolloClient)))
            val cartResponse = CartManager.createCart()
            val cart = cartResponse.data?.cartCreate?.cart

            if (cart != null) {
                SecureSharedPrefHelper.putString(KEY_CART_ID, cart.id)
                SecureSharedPrefHelper.putString(KEY_CHECKOUT_URL, cart.checkoutUrl.toString())

                Log.i("TAG", "Saved Cart ID: ${cart.id}")
                Log.i("TAG", "Saved Checkout URL: ${cart.checkoutUrl}")

                // Link cart to customer
                CartManager.linkCartToCustomer(cart.id, accessToken)
                Log.i("TAG", "Linked cart to customer")
            } else {
                Log.w("TAG", "Cart creation returned null")
            }

            Result.success(customer)
        } catch (e: Exception) {
            Log.e("TAG", "Exception during sign up: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun loginCustomer(
        email: String,
        password: String
    ): Result<LoginCustomerMutation.CustomerAccessToken?> {
        return try {
            val input = CustomerAccessTokenCreateInput(email, password)

            val response = apolloClient
                .mutation(LoginCustomerMutation(input))
                .execute()

            val token = response.data?.customerAccessTokenCreate?.customerAccessToken
            val errors = response.data?.customerAccessTokenCreate?.customerUserErrors.orEmpty()
            Log.i("TAG", "token loginCustomer: $token")
            Log.i("TAG", "error loginCustomer: $errors")

            if (errors.isEmpty() && token != null) {
                Log.i("TAG", "loginCustomer: $token")
                SecureSharedPrefHelper.putString(KEY_CUSTOMER_TOKEN, token.accessToken)
                Log.i("TAG", "Secure Shared: ${SecureSharedPrefHelper.getString(KEY_CUSTOMER_TOKEN)}")
                Result.success(token)
            } else {
                Log.i("TAG", "else loginCustomer: $errors")
                Result.failure(Exception(errors.firstOrNull()?.message ?: "Invalid credentials"))
            }
        } catch (e: Exception) {
            Log.i("TAG", "fail loginCustomer: $e")
            Result.failure(e)
        }
    }
}
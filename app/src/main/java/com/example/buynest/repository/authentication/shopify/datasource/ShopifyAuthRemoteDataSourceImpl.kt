package com.example.buynest.repository.authentication.shopify.datasource

import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import com.apollographql.apollo3.api.Optional
import com.example.buynest.CreateCustomerMutation
import com.example.buynest.LoginCustomerMutation
import com.example.buynest.model.data.remote.graphql.ApolloClient.apolloClient
import com.example.buynest.model.data.remote.rest.RemoteDataSourceImpl
import com.example.buynest.model.data.remote.rest.StripeClient
import com.example.buynest.repository.cart.CartRepository
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
import com.example.buynest.viewmodel.cart.CartViewModel
import com.example.buynest.viewmodel.cart.CartViewModelFactory

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

            Log.i("TAG", "data signUpCustomer: $formattedPhone")
            Log.i("TAG", "data signUpCustomer: $firstName")
            Log.i("TAG", "data signUpCustomer: $email")
            Log.i("TAG", "data signUpCustomer: $password")

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

            if (errors.isEmpty() && customer != null) {
                SecureSharedPrefHelper.putString(KEY_CUSTOMER_ID, customer.id)

                Log.i("TAG", "Secure Shared: ${SecureSharedPrefHelper.getString(KEY_CUSTOMER_ID)}")

                CartManager.setup(CartRepositoryImpl(cartDataSource = CartDataSourceImpl(apolloClient)))
                val cartResponse = CartManager.createCart()
                val cartData = cartResponse.data?.cartCreate?.cart
                val cartId = cartData?.id
                val checkoutUrl = cartData?.checkoutUrl

                if (cartId != null && checkoutUrl != null) {
                    SecureSharedPrefHelper.putString(KEY_CART_ID, cartId)
                    SecureSharedPrefHelper.putString(KEY_CHECKOUT_URL, checkoutUrl.toString())

                    Log.i("TAG", "Secure Shared: ${SecureSharedPrefHelper.getString(KEY_CART_ID)}")
                    Log.i("TAG", "Secure Shared: ${SecureSharedPrefHelper.getString(KEY_CHECKOUT_URL)}")

                    val token = SecureSharedPrefHelper.getString(KEY_CUSTOMER_TOKEN).orEmpty()
                    CartManager.linkCartToCustomer(cartId, token)
                }

                Result.success(customer)
            } else {
                Log.i("TAG", "else signUpCustomer: $errors")
                Result.failure(Exception(errors.firstOrNull()?.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Log.i("TAG", "fail signUpCustomer: $e")
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
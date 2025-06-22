package com.example.buynest.repository.authenticationrepo.shopify.datasource

import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.apollographql.apollo3.api.Optional
import com.example.buynest.CreateCustomerMutation
import com.example.buynest.LoginCustomerMutation
import com.example.buynest.model.remote.graphql.ApolloClient.apolloClient
import com.example.buynest.type.CustomerAccessTokenCreateInput
import com.example.buynest.type.CustomerCreateInput
import com.example.buynest.utils.AppConstants.KEY_CUSTOMER_ID
import com.example.buynest.utils.AppConstants.KEY_CUSTOMER_TOKEN
import com.example.buynest.utils.SecureSharedPrefHelper

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
                Log.i("TAG", "signUpCustomer: ${customer.id}")
                SecureSharedPrefHelper.putString(KEY_CUSTOMER_ID, customer.id)
                Log.i("TAG", "Secure Shared: ${SecureSharedPrefHelper.getString(KEY_CUSTOMER_ID)}")
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
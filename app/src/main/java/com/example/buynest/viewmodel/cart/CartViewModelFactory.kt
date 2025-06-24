package com.example.buynest.viewmodel.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.buynest.BuildConfig
import com.example.buynest.model.data.remote.graphql.ApolloClient
import com.example.buynest.repository.cart.CartRepositoryImpl
import com.example.buynest.repository.cart.datasource.CartDataSourceImpl
import com.example.buynest.repository.order.OrderRepo
import com.example.buynest.utils.constant.CLIENT_BASE_URL
import com.example.buynest.utils.constant.CLIENT_HEADER

class CartViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val apolloClient = ApolloClient.createApollo(
            BASE_URL = CLIENT_BASE_URL,
            ACCESS_TOKEN = BuildConfig.SHOPIFY_ACCESS_TOKEN,
            Header = CLIENT_HEADER
        )
        val repo = CartRepositoryImpl(
            CartDataSourceImpl(apolloClient)
        )

        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(repo,OrderRepo()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


package com.example.buynest.viewmodel.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.buynest.model.data.remote.graphql.ApolloClient.apolloClient
import com.example.buynest.repository.cart.CartRepositoryImpl
import com.example.buynest.repository.cart.datasource.CartDataSourceImpl
import com.example.buynest.repository.order.OrderRepo

class CartViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
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


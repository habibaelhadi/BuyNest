package com.example.buynest.viewmodel.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.buynest.BuildConfig
import com.example.buynest.model.data.remote.graphql.ApolloClient
import com.example.buynest.repository.address.AddressRepositoryImpl
import com.example.buynest.repository.address.datasource.ShopifyAddressDataSourceImpl
import com.example.buynest.utils.constant.CLIENT_BASE_URL
import com.example.buynest.utils.constant.CLIENT_HEADER

class AddressViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val apolloClient = ApolloClient.createApollo(
            BASE_URL = CLIENT_BASE_URL,
            ACCESS_TOKEN = BuildConfig.SHOPIFY_ACCESS_TOKEN,
            Header = CLIENT_HEADER
        )

        val repo = AddressRepositoryImpl(
            ShopifyAddressDataSourceImpl(apolloClient)
        )

        if (modelClass.isAssignableFrom(AddressViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddressViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

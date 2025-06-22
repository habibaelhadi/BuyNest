package com.example.buynest.viewmodel.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.buynest.model.data.remote.graphql.ApolloClient.apolloClient
import com.example.buynest.repository.address.AddressRepositoryImpl
import com.example.buynest.repository.address.datasource.ShopifyAddressDataSourceImpl

class AddressViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
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

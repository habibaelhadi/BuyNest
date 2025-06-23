package com.example.buynest.repository.address

import com.example.buynest.model.entity.AddressModel
import com.example.buynest.repository.address.datasource.ShopifyAddressDataSource
import com.example.buynest.type.MailingAddressInput

class AddressRepositoryImpl(
    private val shopifyAddressDataSource: ShopifyAddressDataSource
) : AddressRepository {

    override suspend fun addAddress(token: String, address: MailingAddressInput): Result<AddressModel> {
        return shopifyAddressDataSource.addAddress(token, address)
    }

    override suspend fun getAllAddresses(token: String): Result<List<AddressModel>> {
        return shopifyAddressDataSource.getAllAddresses(token)
    }

    override suspend fun deleteAddress(token: String, addressId: String): Result<String> {
        return shopifyAddressDataSource.deleteAddress(token, addressId)
    }

    override suspend fun setDefaultAddress(token: String, addressId: String): Result<AddressModel> {
        return shopifyAddressDataSource.setDefaultAddress(token, addressId)
    }

    override suspend fun getDefaultAddress(token: String): Result<AddressModel?> {
        return shopifyAddressDataSource.getDefaultAddress(token)
    }
}


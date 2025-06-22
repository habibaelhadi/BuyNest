package com.example.buynest.repository.address.datasource

import com.example.buynest.model.entity.AddressModel
import com.example.buynest.type.MailingAddressInput

interface ShopifyAddressDataSource {
    suspend fun addAddress(token: String, address: MailingAddressInput): Result<AddressModel>
    suspend fun getAllAddresses(token: String): Result<List<AddressModel>>
    suspend fun deleteAddress(token: String, addressId: String): Result<String>
    suspend fun setDefaultAddress(token: String, addressId: String): Result<AddressModel>
    suspend fun getDefaultAddress(token: String): Result<AddressModel?>
}

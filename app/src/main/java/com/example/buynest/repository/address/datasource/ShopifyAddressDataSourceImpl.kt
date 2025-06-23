package com.example.buynest.repository.address.datasource

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.example.buynest.*
import com.example.buynest.AddCustomerAddressMutation.CustomerAddress
import com.example.buynest.model.entity.AddressModel
import com.example.buynest.type.MailingAddressInput

class ShopifyAddressDataSourceImpl(
    private val apolloClient: ApolloClient
) : ShopifyAddressDataSource {

    override suspend fun addAddress(token: String, address: MailingAddressInput): Result<AddressModel> {
        Log.d("ShopifyAddressDS", "Adding address: $address")

        val response = apolloClient.mutation(
            AddCustomerAddressMutation(token, address)
        ).execute()

        val data = response.data?.customerAddressCreate?.customerAddress

        return if (data != null) {
            val model = data.toAddressModel()
            Log.d("ShopifyAddressDS", "Address added successfully: $model")
            Result.success(model)
        } else {
            Log.e(
                "ShopifyAddressDS",
                "Add address failed. Data: ${response.data}, Errors: ${response.errors}"
            )
            Result.failure(Exception(response.errors?.firstOrNull()?.message ?: "Add address failed"))
        }
    }

    override suspend fun getAllAddresses(token: String): Result<List<AddressModel>> {
        Log.d("ShopifyAddressDS", "Fetching all addresses")
        val response = apolloClient.query(
            GetCustomerAddressesQuery(token)
        ).execute()

        Log.d("ShopifyAddressDS", "Get all addresses response: $response")

        val edges = response.data?.customer?.addresses?.edges
        val list = edges?.mapNotNull { it.node?.toAddressModel() } ?: emptyList()
        Log.d("ShopifyAddressDS", "Fetched ${list.size} addresses")
        return Result.success(list)
    }

    override suspend fun deleteAddress(token: String, addressId: String): Result<String> {
        Log.d("ShopifyAddressDS", "Deleting address with ID: $addressId")
        val response = apolloClient.mutation(
            CustomerAddressDeleteMutation(token, addressId)
        ).execute()

        Log.d("ShopifyAddressDS", "Delete address response: $response")

        val deletedId = response.data?.customerAddressDelete?.deletedCustomerAddressId
        return if (deletedId != null) {
            Log.d("ShopifyAddressDS", "Deleted address ID: $deletedId")
            Result.success(deletedId)
        } else {
            val error = response.errors?.firstOrNull()?.message ?: "Delete failed"
            Log.e("ShopifyAddressDS", "Delete address error: $error")
            Result.failure(Exception(error))
        }
    }

    override suspend fun setDefaultAddress(token: String, addressId: String): Result<AddressModel> {
        Log.d("ShopifyAddressDS", "Setting default address ID: $addressId")
        val response = apolloClient.mutation(
            SetDefaultAddressMutation(token, addressId)
        ).execute()

        Log.d("ShopifyAddressDS", "Set default address response: $response")

        val defaultAddress = response.data?.customerDefaultAddressUpdate?.customer?.defaultAddress
        return if (defaultAddress != null) {
            val model = defaultAddress.toAddressModel()
            Log.d("ShopifyAddressDS", "Set default address success: $model")
            Result.success(model)
        } else {
            val error = response.errors?.firstOrNull()?.message ?: "Set default failed"
            Log.e("ShopifyAddressDS", "Set default address error: $error")
            Result.failure(Exception(error))
        }
    }

    override suspend fun getDefaultAddress(token: String): Result<AddressModel?> {
        Log.d("ShopifyAddressDS", "Fetching default address")
        val response = apolloClient.query(
            GetDefaultAddressQuery(token)
        ).execute()

        Log.d("ShopifyAddressDS", "Get default address response: $response")

        val address = response.data?.customer?.defaultAddress
        val model = address?.toAddressModel()
        Log.d("ShopifyAddressDS", "Default address: $model")
        return Result.success(model)
    }

    private fun CustomerAddress.toAddressModel() = AddressModel(
        id = this.id,
        firstName = "no name",
        lastName = "no name",
        address1 = this.address1,
        address2 = this.address2,
        city = this.city,
        country = this.country,
        phone = "-1"
    )

    private fun GetCustomerAddressesQuery.Node.toAddressModel() = AddressModel(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        address1 = this.address1,
        address2 = this.address2,
        city = this.city,
        country = this.country,
        phone = this.phone
    )

    private fun SetDefaultAddressMutation.DefaultAddress.toAddressModel() = AddressModel(
        id = this.id,
        firstName = "no name",
        lastName = "no name",
        address1 = this.address1,
        address2 = "no found address",
        city = "no found city",
        country = "no found country",
        phone = "-1"
    )

    private fun GetDefaultAddressQuery.DefaultAddress.toAddressModel() = AddressModel(
        id = this.id,
        firstName = this.firstName,
        lastName = "no name",
        address1 = this.address1,
        address2 = this.address2,
        city = this.city,
        country = this.country,
        phone = this.phone
    )
}

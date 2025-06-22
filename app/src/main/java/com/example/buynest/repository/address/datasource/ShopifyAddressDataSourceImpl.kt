package com.example.buynest.repository.address.datasource

import com.apollographql.apollo3.ApolloClient
import com.example.buynest.*
import com.example.buynest.AddCustomerAddressMutation.CustomerAddress
import com.example.buynest.model.entity.AddressModel
import com.example.buynest.type.MailingAddressInput

class ShopifyAddressDataSourceImpl(
    private val apolloClient: ApolloClient
) : ShopifyAddressDataSource {

    override suspend fun addAddress(token: String, address: MailingAddressInput): Result<AddressModel> {
        val response = apolloClient.mutation(
            AddCustomerAddressMutation(token, address)
        ).execute()

        val data = response.data?.customerAddressCreate?.customerAddress
        return if (data != null) {
            Result.success(data.toAddressModel())
        } else {
            Result.failure(Exception(response.errors?.firstOrNull()?.message ?: "Add address failed"))
        }
    }

    override suspend fun getAllAddresses(token: String): Result<List<AddressModel>> {
        val response = apolloClient.query(
            GetCustomerAddressesQuery(token)
        ).execute()

        val edges = response.data?.customer?.addresses?.edges
        val list = edges?.mapNotNull { it.node?.toAddressModel() } ?: emptyList()
        return Result.success(list)
    }

    override suspend fun deleteAddress(token: String, addressId: String): Result<String> {
        val response = apolloClient.mutation(
            CustomerAddressDeleteMutation(token, addressId)
        ).execute()

        val deletedId = response.data?.customerAddressDelete?.deletedCustomerAddressId
        return if (deletedId != null) Result.success(deletedId)
        else Result.failure(Exception(response.errors?.firstOrNull()?.message ?: "Delete failed"))
    }

    override suspend fun setDefaultAddress(token: String, addressId: String): Result<AddressModel> {
        val response = apolloClient.mutation(
            SetDefaultAddressMutation(token, addressId)
        ).execute()

        val defaultAddress = response.data?.customerDefaultAddressUpdate?.customer?.defaultAddress
        return if (defaultAddress != null) {
            Result.success(defaultAddress.toAddressModel())
        } else {
            Result.failure(Exception(response.errors?.firstOrNull()?.message ?: "Set default failed"))
        }
    }

    override suspend fun getDefaultAddress(token: String): Result<AddressModel?> {
        val response = apolloClient.query(
            GetDefaultAddressQuery(token)
        ).execute()

        val address = response.data?.customer?.defaultAddress
        return Result.success(address?.toAddressModel())
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


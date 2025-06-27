package com.example.buynest.repository.address

import org.junit.Assert.*
import com.apollographql.apollo3.api.Optional
import com.example.buynest.model.entity.AddressModel
import com.example.buynest.repository.address.datasource.ShopifyAddressDataSource
import com.example.buynest.type.MailingAddressInput
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AddressRepositoryImplTest {
    private lateinit var dataSource: ShopifyAddressDataSource
    private lateinit var repository: AddressRepository

    private val dummyToken = "dummy_token"
    private val dummyAddressId = "addr123"
    private val dummyInput = MailingAddressInput(
        address1 = Optional.Present("123 Main St"),
        city = Optional.Present("Alexandria"),
        country = Optional.Present("Egypt")
    )

    private val dummyAddress = AddressModel(
        id = "addr123",
        firstName = "Home",
        lastName = "",
        city = "Alexandria",
        address1 = "123 Main St",
        address2 = "",
        country = "Egypt",
        phone = "01012345678"
    )

    @Before
    fun setup() {
        dataSource = mockk(relaxed = true)
        repository = AddressRepositoryImpl(dataSource)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `test addAddress`() = runTest {
        coEvery { dataSource.addAddress(dummyToken, dummyInput) } returns Result.success(dummyAddress)

        val result = repository.addAddress(dummyToken, dummyInput)

        assertEquals(Result.success(dummyAddress), result)
        coVerify { dataSource.addAddress(dummyToken, dummyInput) }
    }

    @Test
    fun `test getAllAddresses`() = runTest {
        val expected = listOf(dummyAddress)
        coEvery { dataSource.getAllAddresses(dummyToken) } returns Result.success(expected)

        val result = repository.getAllAddresses(dummyToken)

        assertEquals(Result.success(expected), result)
        coVerify { dataSource.getAllAddresses(dummyToken) }
    }

    @Test
    fun `test deleteAddress`() = runTest {
        coEvery { dataSource.deleteAddress(dummyToken, dummyAddressId) } returns Result.success("Deleted")

        val result = repository.deleteAddress(dummyToken, dummyAddressId)

        assertEquals(Result.success("Deleted"), result)
        coVerify { dataSource.deleteAddress(dummyToken, dummyAddressId) }
    }

    @Test
    fun `test updateAddress`() = runTest {
        coEvery {
            dataSource.updateAddress(dummyToken, dummyAddressId, dummyInput)
        } returns Result.success(dummyAddress)

        val result = repository.updateAddress(dummyToken, dummyAddressId, dummyInput)

        assertEquals(Result.success(dummyAddress), result)
        coVerify { dataSource.updateAddress(dummyToken, dummyAddressId, dummyInput) }
    }

    @Test
    fun `test setDefaultAddress`() = runTest {
        coEvery { dataSource.setDefaultAddress(dummyToken, dummyAddressId) } returns Result.success(dummyAddress)

        val result = repository.setDefaultAddress(dummyToken, dummyAddressId)

        assertEquals(Result.success(dummyAddress), result)
        coVerify { dataSource.setDefaultAddress(dummyToken, dummyAddressId) }
    }

    @Test
    fun `test getDefaultAddress`() = runTest {
        coEvery { dataSource.getDefaultAddress(dummyToken) } returns Result.success(dummyAddress)

        val result = repository.getDefaultAddress(dummyToken)

        assertEquals(Result.success(dummyAddress), result)
        coVerify { dataSource.getDefaultAddress(dummyToken) }
    }
}

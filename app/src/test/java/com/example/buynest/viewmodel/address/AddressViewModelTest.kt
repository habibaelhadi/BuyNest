package com.example.buynest.viewmodel.address

import android.util.Log
import com.apollographql.apollo3.api.Optional
import com.example.buynest.model.entity.AddressModel
import com.example.buynest.repository.address.AddressRepository
import com.example.buynest.type.MailingAddressInput
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class AddressViewModelTest {

    private lateinit var repository: AddressRepository
    private lateinit var viewModel: AddressViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = AddressViewModel(repository)

        mockkStatic(Log::class)
        io.mockk.every { Log.d(any(), any()) } returns 0
        io.mockk.every { Log.e(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    @Test
    fun `loadAddresses should update state on success`() = runTest {
        val token = "testToken"
        val mockList = listOf(AddressModel("1", "youssef", "", "Street1", "", "City", "Country", "12345"))
        coEvery { repository.getAllAddresses(token) } returns Result.success(mockList)

        viewModel.loadAddresses(token)
        advanceUntilIdle()

        assertEquals(mockList, viewModel.addresses.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `addAddress should trigger loadAddresses on success`() = runTest {
        val token = "testToken"
        val input = MailingAddressInput(
            firstName = Optional.Present("youssef"),
            lastName = Optional.Present(""),
            address1 = Optional.Present("Street 1"),
            address2 = Optional.Present(""),
            city = Optional.Present("City"),
            country = Optional.Present("Country"),
            zip = Optional.Absent,
            phone = Optional.Present("12345")
        )

        val fakeAddress = AddressModel(
            id = "1",
            firstName = "youssef",
            lastName = "",
            address1 = "Street 1",
            address2 = "",
            city = "City",
            country = "Country",
            phone = "12345"
        )

        coEvery { repository.addAddress(token, input) } returns Result.success(fakeAddress)
        coEvery { repository.getAllAddresses(token) } returns Result.success(emptyList())

        viewModel.addAddress(token, input)
        advanceUntilIdle()

        assertEquals(emptyList(), viewModel.addresses.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `loadDefaultAddress should update state`() = runTest {
        val token = "token"
        val address = AddressModel("id", "A", "B", "St", "", "C", "D", "00000")
        coEvery { repository.getDefaultAddress(token) } returns Result.success(address)

        viewModel.loadDefaultAddress(token)
        advanceUntilIdle()

        assertEquals(address, viewModel.defaultAddress.value)
        assertNull(viewModel.error.value)
    }
}

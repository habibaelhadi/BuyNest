package com.example.buynest.repository.authentication.shopify

import com.example.buynest.repository.authentication.shopify.datasource.ShopifyAuthRemoteDataSource
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class ShopifyAuthRepositoryImplTest{
    private val remoteDataSource: ShopifyAuthRemoteDataSource = mockk(relaxed = true)
    private lateinit var repository: ShopifyAuthRepository

    @Before
    fun setup(){
        repository = ShopifyAuthRepositoryImpl(remoteDataSource)
    }

    @After
    fun tearDown(){
        unmockkAll()
    }

    @Test
    fun `test register`() = runTest  {
        //given
        val fullName = "fullName"
        val email = "email"
        val password = "password"
        val phone = "phone"

        //when
        repository.register(fullName, email, password, phone)

        //then
        coVerify { remoteDataSource.signUpCustomer(fullName, email, password, phone) }
    }

    @Test
    fun `test login` () = runTest {
        //given
        val email = "email"
        val password = "password"
        val cartId = "cartId"

        //when
        repository.login(email, password, cartId)

        //then
        coVerify { remoteDataSource.loginCustomer(email, password, cartId) }
    }
}
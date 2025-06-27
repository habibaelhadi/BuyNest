package com.example.buynest.repository.authentication.firebase

import com.example.buynest.repository.authentication.firebase.datasource.IFirebaseDataSource
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test


class FirebaseRepositoryImplTest{
    private val firebaseDataSource: IFirebaseDataSource = mockk(relaxed = true)
    private lateinit var firebaseRepository: FirebaseRepository

    @Before
    fun setup(){
       firebaseRepository = FirebaseRepositoryImpl(firebaseDataSource)
    }

    @After
    fun tearDown(){
        unmockkAll()
    }

    @Test
    fun `test signup`(){
        //given
        val name = "name"
        val phone = "01234567890"
        val email = "test@gmail.com"
        val password = "123456"

        //when
        firebaseRepository.signup(name, phone, email, password)

        //then
        verify { firebaseDataSource.signup(name, phone, email, password) }
    }

    @Test
    fun `test login`(){
        //given
        val email = "test@gmail.com"
        val password = "123456"

        //when
        firebaseRepository.login(email, password)

        //then
        verify { firebaseDataSource.login(email, password) }
    }

    @Test
    fun `test logout`(){
        //when
        firebaseRepository.logout()
    }

    @Test
    fun `test saveShopifyTokenToFireStore`(){
        //given
        val customerToken = "customerToken"
        val customerId = "customerId"
        val cartId = "cartId"
        val checkOutKey = "checkOutKey"

        //when
        firebaseRepository.saveShopifyTokenToFireStore(customerToken, customerId, cartId, checkOutKey)

        //then
        verify { firebaseDataSource.saveShopifyTokenToFireStore(customerToken, customerId, cartId, checkOutKey) }
    }
}
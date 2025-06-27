package com.example.buynest.viewmodel.authentication

import android.util.Log
import com.example.buynest.repository.authentication.AuthenticationRepo
import com.example.buynest.utils.strategies.AuthenticationStrategy
import com.example.buynest.utils.validators.ValidationHandler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test


class AuthenticationViewModelTest{
    private lateinit var viewModel: AuthenticationViewModel
    private lateinit var authRepo: AuthenticationRepo
    private lateinit var strategy: AuthenticationStrategy

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0

        authRepo = mockk(relaxed = true)
        strategy = mockk()
        viewModel = AuthenticationViewModel(authRepo)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `test resetPassword`() = runTest {
        //given
        val email = "test@gmail.com"
        coEvery { authRepo.sendResetPasswordEmail(email) } returns Result.success("Password reset email sent.")

        //when
        viewModel.resetPassword(email)

        //then
        coVerify { authRepo.sendResetPasswordEmail(email) }
    }

    @Test
    fun `test authenticate`() = runTest {
        // given
        coEvery { strategy.authenticate(authRepo) } returns Result.success(Unit)

        // when
        viewModel.authenticate(strategy)

        // then
        coVerify { strategy.authenticate(authRepo) }
    }

    @Test
    fun `test logout` () = runTest {
        // Given
        coEvery { authRepo.logout() } returns Result.success(Unit)

        // When
        viewModel.logout()

        // Then
        val result = viewModel.message.first()
        assertEquals("Success", result)
    }
}
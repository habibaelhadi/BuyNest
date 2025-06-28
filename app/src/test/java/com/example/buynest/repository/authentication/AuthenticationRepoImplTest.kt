package com.example.buynest.repository.authentication

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.example.buynest.model.repository.authentication.AuthenticationRepoImpl
import com.example.buynest.model.state.FirebaseResponse
import com.example.buynest.model.repository.authentication.firebase.FirebaseRepository
import com.example.buynest.model.repository.authentication.shopify.ShopifyAuthRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import kotlin.test.Test

class AuthenticationRepoImplTest{
    private lateinit var firebaseRepository: FirebaseRepository
    private lateinit var shopifyRepository: ShopifyAuthRepository
    private lateinit var authenticationRepoImpl: AuthenticationRepoImpl

    @Before
    fun setup() {
        firebaseRepository = mockk(relaxed = true)
        shopifyRepository = mockk(relaxed = true)
        authenticationRepoImpl = AuthenticationRepoImpl(firebaseRepository, shopifyRepository)
    }

    @After
    fun tearDown(){
        unmockkAll()
    }

    @Test
    fun `sendResetPasswordEmail - success`() = runTest {
        // Given
        val email = "test@example.com"
        val firebaseResponseSlot = slot<FirebaseResponse>()

        every { firebaseRepository.setFirebaseResponse(capture(firebaseResponseSlot)) } just Runs

        every { firebaseRepository.sendPasswordResetEmail(email) } answers {
            // simulate success callback
            firebaseResponseSlot.captured.onResponseSuccess("Reset email sent")
        }

        // When
        val result = authenticationRepoImpl.sendResetPasswordEmail(email)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Password reset email sent.", result.getOrNull())
    }

    @Test
    fun `logInWithGoogle - success`() = runTest {
        // Given
        val context = mockk<Context>(relaxed = true)
        val intent = mockk<Intent>(relaxed = true)
        val launcher = mockk<ActivityResultLauncher<Intent>>(relaxed = true)

        every { firebaseRepository.getGoogleSignInIntent(context) } returns intent

        // When
        val result = authenticationRepoImpl.logInWithGoogle(context, launcher)

        // Then
        verify { launcher.launch(intent) }
        assertTrue(result.isSuccess)
    }

    @Test
    fun `logout - success`() = runTest {
        // Given
        val firebaseResponseSlot = slot<FirebaseResponse>()

        every { firebaseRepository.setFirebaseResponse(capture(firebaseResponseSlot)) } just Runs

        every { firebaseRepository.logout() } answers {
            // simulate triggering success callback
            firebaseResponseSlot.captured.onResponseSuccess("Logged out")
        }

        // When
        val result = authenticationRepoImpl.logout()

        // Then
        assertTrue(result.isSuccess)
    }
}
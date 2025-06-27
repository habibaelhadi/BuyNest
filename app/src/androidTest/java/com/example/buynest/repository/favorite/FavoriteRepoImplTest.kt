package com.example.buynest.repository.favorite

import com.example.buynest.model.state.FirebaseResponse
import com.example.buynest.repository.favorite.favFirebase.FavFirebase
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class FavoriteRepoImplTest{
    private lateinit var repo: FavoriteRepoImpl
    private lateinit var firebaseResponse: FirebaseResponse

    @Before
    fun setup() {
        repo = FavoriteRepoImpl()
        firebaseResponse = mockk(relaxed = true)
        repo.setFirebaseResponse(firebaseResponse)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun addToFavorite_returns_success () = runTest {
        val slot = slot<FirebaseResponse>()

        mockkObject(FavFirebase)
        every { FavFirebase.setFirebaseResponse(capture(slot)) } answers {
            slot.captured.onResponseSuccess("Added")
        }
        every { FavFirebase.addToFavorite("123") } just Runs

        val result = repo.addToFavorite("123")
        assertTrue(result.isSuccess)
    }

    @Test
    fun removeFromFavorite_returns_failure() = runTest {
        val slot = slot<FirebaseResponse>()

        mockkObject(FavFirebase)
        every { FavFirebase.setFirebaseResponse(capture(slot)) } answers {
            slot.captured.onResponseFailure("Failed to remove")
        }
        every { FavFirebase.removeFromFavorite("123") } just Runs

        val result = repo.removeFromFavorite("123")
        assertTrue(result.isFailure)
        assertEquals("Failed to remove", result.exceptionOrNull()?.message)
    }

    @Test
    fun getAllFavorites_returns_list_on_success() = runTest {
        val slot = slot<FirebaseResponse>()

        mockkObject(FavFirebase)
        every { FavFirebase.setFirebaseResponse(capture(slot)) } answers {
            slot.captured.onResponseSuccess(listOf("id1", "id2"))
        }
        every { FavFirebase.getAllFavorites() } just Runs

        val result = repo.getAllFavorites()
        assertTrue(result.isSuccess)
        assertEquals(listOf("id1", "id2"), result.getOrNull())
    }
}
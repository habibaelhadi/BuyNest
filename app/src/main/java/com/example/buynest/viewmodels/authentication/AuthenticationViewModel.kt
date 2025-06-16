package com.example.buynest.viewmodels.authentication

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buynest.repos.authenticationrepo.AuthenticationRepo
import com.example.buynest.repos.authenticationrepo.firebase.Firebase
import com.example.buynest.utils.strategies.AuthenticationStrategy
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.play.integrity.internal.l
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class AuthenticationViewModel(private val authRepo: AuthenticationRepo) : ViewModel() {

    private lateinit var googleLauncher: ActivityResultLauncher<Intent>

    fun setGoogleLauncher(launcher: ActivityResultLauncher<Intent>) {
        googleLauncher = launcher
    }

    fun handleGoogleSignInResult(requestCode: Int, data: Intent?) {
        if (requestCode != 123) return
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null && account.idToken != null) {
                signInWithGoogle(account.idToken!!)
            } else {
                //onFailure.invoke("ID token is null")
            }
        } catch (e: ApiException) {
            //onFailure.invoke("Google Sign-In failed: ${e.message}")
        }
    }

    private fun signInWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                   // onSuccess.invoke()
                } else {
                    //onFailure.invoke(task.exception?.message ?: "Sign-in failed")
                }
            }
    }

    fun authenticate(strategy: AuthenticationStrategy){
        viewModelScope.launch {
            strategy.authenticate(authRepo)
        }
    }

    class AuthenticationViewModelFactory(private val repo: AuthenticationRepo): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthenticationViewModel(repo) as T
        }
    }
}
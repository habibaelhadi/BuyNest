package com.example.buynest.viewmodels.authentication

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buynest.repos.authenticationrepo.AuthenticationRepo
import com.example.buynest.repos.authenticationrepo.firebase.Firebase
import com.example.buynest.utils.sharedPreferences.SharedPreferencesImpl
import com.example.buynest.utils.strategies.AuthenticationStrategy
import com.example.buynest.utils.validators.LoginValidator
import com.example.buynest.utils.validators.SignUpValidator
import com.example.buynest.utils.validators.ValidationHandler
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.play.integrity.internal.l
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AuthenticationViewModel(private val authRepo: AuthenticationRepo) : ViewModel() {

    private lateinit var googleLauncher: ActivityResultLauncher<Intent>
    private var validationChain: ValidationHandler = SignUpValidator()
    private val mutableMessage = MutableSharedFlow<String>()
    val message = mutableMessage.asSharedFlow()

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
        validationChain.setNext(LoginValidator())
        viewModelScope.launch {
            val validationResult = validationChain.handle(strategy)
            if (validationResult != null) {
                mutableMessage.emit(validationResult)
                return@launch
            }
            val result = strategy.authenticate(authRepo)
            if (result.isSuccess) {
                mutableMessage.emit("Success")
            }else{
                mutableMessage.emit(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    class AuthenticationViewModelFactory(private val repo: AuthenticationRepo): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthenticationViewModel(repo) as T
        }
    }
}
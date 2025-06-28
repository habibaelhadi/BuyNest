package com.example.buynest.viewmodel.authentication

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buynest.model.repository.FirebaseAuthObject
import com.example.buynest.model.repository.authentication.AuthenticationRepo
import com.example.buynest.utils.strategies.AuthenticationStrategy
import com.example.buynest.utils.strategies.GoogleAuthenticationStrategy
import com.example.buynest.utils.validators.GoogleValidator
import com.example.buynest.utils.validators.LoginValidator
import com.example.buynest.utils.validators.SignUpValidator
import com.example.buynest.utils.validators.ValidationHandler
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AuthenticationViewModel(private val authRepo: AuthenticationRepo) : ViewModel() {

    private lateinit var googleLauncher: ActivityResultLauncher<Intent>
    private var validationChain: ValidationHandler = GoogleValidator().apply {
        setNext(LoginValidator().apply {
            setNext(SignUpValidator())
        })
    }
    private val _mutableMessage = MutableSharedFlow<String>()
    val message = _mutableMessage.asSharedFlow()

    private lateinit var googleStrategy: GoogleAuthenticationStrategy

    fun resetPassword(email: String) {
        viewModelScope.launch {
            if (email.isEmpty()) {
                _mutableMessage.emit("Email cannot be empty")
                return@launch
            }
            val result = authRepo.sendResetPasswordEmail(email)
            if (result.isSuccess){
                _mutableMessage.emit("Success")
            }else{
               _mutableMessage.emit(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun setGoogleStrategy(strategy: GoogleAuthenticationStrategy): String? {
        return GoogleValidator().validate(strategy).also {
            if (it == null) googleStrategy = strategy
        }
    }

    fun getGoogleSignInIntent(context: Context): Intent? {
        return authRepo.getGoogleSignInIntent(context)
    }

    fun setGoogleLauncher(launcher: ActivityResultLauncher<Intent>) {
        googleLauncher = launcher
    }

    fun handleGoogleSignInResult(requestCode: Int, data: Intent?, context: Context) {
        if (requestCode != 123) return
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null && account.idToken != null) {
                signInWithGoogle(account.idToken!!,context)
            } else {
                viewModelScope.launch {
                    _mutableMessage.emit("Google Sign-In failed: ID token is null")
                }
            }
        } catch (e: ApiException) {
            viewModelScope.launch {
                _mutableMessage.emit("Google Sign-In failed: ${e.message}")
            }
        }
    }

    private fun signInWithGoogle(idToken: String, context: Context) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val auth = FirebaseAuthObject.getAuth()
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                viewModelScope.launch {
                    if (task.isSuccessful) {
                        authRepo.saveGoogleUserToFireStore(context = context)
                        _mutableMessage.emit("Success")
                    } else {
                        _mutableMessage.emit(task.exception?.message ?: "Google Sign-In failed")
                    }
                }
            }
    }

    fun authenticate(strategy: AuthenticationStrategy){
        viewModelScope.launch {
            val validationResult = validationChain.handle(strategy)
            if (validationResult != null) {
                _mutableMessage.emit(validationResult)
                return@launch
            }
            val result = strategy.authenticate(authRepo)
            if (result.isSuccess) {
                _mutableMessage.emit("Success")
            }else{
                _mutableMessage.emit(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun logout(){
        viewModelScope.launch {
            val result = authRepo.logout()
            if (result.isSuccess){
                _mutableMessage.emit("Success")
            }else{
                _mutableMessage.emit(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
}
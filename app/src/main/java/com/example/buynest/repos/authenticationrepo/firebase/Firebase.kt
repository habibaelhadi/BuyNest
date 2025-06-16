package com.example.buynest.repos.authenticationrepo.firebase

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.StringRes
import com.example.buynest.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class Firebase private constructor() {
    private var googleSignInClient: GoogleSignInClient? = null
    private var firebaseResponse: FirebaseResponse? = null
    private var message: String? = null

    init {
        auth = FirebaseAuth.getInstance()
    }

    fun setFirebaseResponse(firebaseResponse: FirebaseResponse?) {
        this.firebaseResponse = firebaseResponse
    }

    fun connectToGoogle(context: Context) {
        Log.i("TAG", "connectToGoogle: ")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun signup(name: String, phone: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            user.sendEmailVerification().addOnCompleteListener { verifyTask ->
                                if (verifyTask.isSuccessful) {

                                    val userMap = hashMapOf(
                                        "name" to name,
                                        "email" to email,
                                        "phone" to phone
                                    )
                                    FirebaseFirestore.getInstance()
                                        .collection("users")
                                        .document(user.uid)
                                        .set(userMap)
                                        .addOnSuccessListener {
                                            message = "Verification email sent to ${user.email}"
                                            firebaseResponse?.onResponseSuccess(message)
                                        }
                                        .addOnFailureListener { e ->
                                            message = e.message
                                            firebaseResponse?.onResponseFailure(message)
                                        }
                                } else {
                                    message = verifyTask.exception?.message
                                    firebaseResponse?.onResponseFailure(message)
                                }
                            }
                        } else {
                            message = updateTask.exception?.message
                            firebaseResponse?.onResponseFailure(message)
                        }
                    }
                } else {
                    message = task.exception?.message
                    firebaseResponse?.onResponseFailure(message)
                }
            }
    }



    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        message = "success"
                        firebaseResponse?.onResponseSuccess(message)
                    } else {
                        message = "Please verify your email before logging in."
                        firebaseResponse?.onResponseFailure(message)
                    }
                } else {
                    message = task.exception?.message
                    firebaseResponse?.onResponseFailure(message)
                }
            }
    }

    fun logout() {
        auth.signOut()
    }

    fun getGoogleSignInIntent(): Intent? {
        return googleSignInClient?.signInIntent
    }

    companion object {
        lateinit var auth: FirebaseAuth
        private var firebase: Firebase? = null
        val instance: Firebase
            get() {
                if (firebase == null) {
                    firebase = Firebase()
                }
                return firebase!!
            }
    }
}
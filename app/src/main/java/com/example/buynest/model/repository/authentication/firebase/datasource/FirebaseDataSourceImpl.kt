package com.example.buynest.model.repository.authentication.firebase.datasource

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.buynest.R
import com.example.buynest.model.repository.FirebaseAuthObject
import com.example.buynest.model.state.FirebaseResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseDataSourceImpl : IFirebaseDataSource {
    private val auth: FirebaseAuth = FirebaseAuthObject.getAuth()
    private var googleSignInClient: GoogleSignInClient? = null
    private var firebaseResponse: FirebaseResponse? = null
    private var message: String? = null

    override fun setFirebaseResponse(firebaseResponse: FirebaseResponse?) {
        this.firebaseResponse = firebaseResponse
    }

//    private fun connectToGoogle(context: Context) {
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(context.getString(R.string.default_web_client_id))
//            .requestEmail()
//            .requestProfile()
//            .build()
//        googleSignInClient = GoogleSignIn.getClient(context, gso)
//    }

    override fun signup(name: String, phone: String, email: String, password: String) {
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
                                        "phone" to phone,
                                        "cartId" to "",
                                        "createdAt" to System.currentTimeMillis()
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

    override fun login(email: String, password: String) {
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

    override fun logout() {
        auth.signOut()
        firebaseResponse?.onResponseSuccess("Logged out")
    }

    override fun getGoogleSignInIntent(context: Context): Intent? {
        //connectToGoogle(context)
        return googleSignInClient?.signInIntent
    }

    override fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseResponse?.onResponseSuccess("Success")
                } else {
                    firebaseResponse?.onResponseFailure(task.exception?.message)
                }
            }
    }

    override fun saveGoogleUserToFireStore(context: Context) {
        val user = auth.currentUser
        user?.let {
            val name = it.displayName ?: "No Name"
            val email = it.email ?: "No Email"
            val phone = it.phoneNumber ?: "No Phone"

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
                    firebaseResponse?.onResponseSuccess("Google user saved to Firestore")
                }
                .addOnFailureListener { e ->
                    firebaseResponse?.onResponseFailure(e.message)
                }
        } ?: run {
            firebaseResponse?.onResponseFailure("No authenticated Google user found")
        }
    }

    override fun saveShopifyTokenToFireStore(
        customerToken: String,
        customerId: String,
        cartId: String,
        checkOutKey: String
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val data = hashMapOf(
                "customerToken" to customerToken,
                "customerId" to customerId,
                "cartId" to cartId,
                "checkOutKey" to checkOutKey
            )

            FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.uid)
                .update(data as Map<String, String>)
                .addOnSuccessListener {
                    Log.i("TAG", "Shopify token saved successfully")
                    firebaseResponse?.onResponseSuccess("Shopify token saved")
                }
                .addOnFailureListener { e ->
                    Log.e("TAG", "Failed to save Shopify token: ${e.message}")
                    firebaseResponse?.onResponseFailure(e.message)
                }
        } else {
            Log.e("TAG", "No Firebase user to link Shopify token")
            firebaseResponse?.onResponseFailure("User not logged in")
        }
    }

    override suspend fun getCartId(): String = suspendCoroutine { continuation ->
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val cartId = document.getString("cartId") ?: ""
                        Log.i("TAG", "Firestore CartId: $cartId")
                        continuation.resume(cartId)
                    } else {
                        Log.w("TAG", "Document for user ${user.uid} does not exist.")
                        continuation.resume("")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("TAG", "Failed to fetch CartId: ${e.message}", e)
                    continuation.resume("")
                }
        } else {
            Log.e("TAG", "No authenticated user found for fetching cartId.")
            continuation.resume("")
        }
    }

}

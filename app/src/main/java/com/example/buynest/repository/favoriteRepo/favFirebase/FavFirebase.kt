package com.example.buynest.repository.favoriteRepo.favFirebase

import com.example.buynest.repository.FirebaseAuthObject
import com.example.buynest.model.state.FirebaseResponse
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

object FavFirebase {
    val auth = FirebaseAuthObject.getAuth()
    private var firebaseResponse: FirebaseResponse? = null

    fun setFirebaseResponse(firebaseResponse: FirebaseResponse?) {
        FavFirebase.firebaseResponse = firebaseResponse
    }

    fun addToFavorite(productId: String){
        val userId = auth.currentUser?.uid!!
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId)
            .update("fav", FieldValue.arrayUnion(productId))
            .addOnSuccessListener {
                firebaseResponse?.onResponseSuccess("Success")
            }
            .addOnFailureListener {
                firebaseResponse?.onResponseFailure(it.message)
            }
    }

    fun removeFromFavorite(productId: String) {
        val userId = auth.currentUser?.uid!!
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId)
            .update("fav", FieldValue.arrayRemove(productId))
            .addOnSuccessListener {
                firebaseResponse?.onResponseSuccess("Success")
            }
            .addOnFailureListener {
                firebaseResponse?.onResponseFailure(it.message)
            }
    }

    fun isFavorite(productId: String){
        val db = FirebaseFirestore.getInstance()
        val userId = auth.currentUser?.uid!!
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val favList = document.get("fav") as? List<*>
                    if (favList != null && favList.contains(productId)) {
                        firebaseResponse?.onResponseSuccess(true)
                    } else {
                        firebaseResponse?.onResponseSuccess(false)
                    }
                } else {
                    firebaseResponse?.onResponseFailure("User document not found")
                }
            }
            .addOnFailureListener { e ->
               firebaseResponse?.onResponseFailure(e.message)
            }
    }

    fun getAllFavorites(){
        val db = FirebaseFirestore.getInstance()
        val userId = auth.currentUser?.uid!!
        var favorites: List<String> = emptyList()

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val favList = document.get("fav") as? List<*>
                    if (favList != null) {
                         favorites = favList.filterIsInstance<String>()
                        firebaseResponse?.onResponseSuccess(favorites)
                    } else {
                        firebaseResponse?.onResponseSuccess(favorites)
                    }
                } else {
                    firebaseResponse?.onResponseFailure("User document not found")
                }
            }
            .addOnFailureListener { e ->
                firebaseResponse?.onResponseFailure(e.message)
            }
    }
}
package com.dawnlight.tidyme.data.firebase

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object FirebaseManager {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    fun initialize(context: Context) {
        FirebaseApp.initializeApp(context)
        auth = Firebase.auth
        firestore = Firebase.firestore

        // Enable offline persistence
        firestore.firestoreSettings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }

    suspend fun ensureAuthenticated(): String {
        val currentUser = auth.currentUser

        return if (currentUser != null) {
            currentUser.uid
        } else {
            val result = auth.signInAnonymously().await()
            result.user?.uid ?: throw IllegalStateException("Failed to authenticate anonymously")
        }
    }

    fun getFirestore(): FirebaseFirestore = firestore
}

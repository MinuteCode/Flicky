package com.minutecode.flicky.ui.library

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.minutecode.flicky.model.omdb.FullMovie
import com.minutecode.flicky.model.user.User

class LibraryViewModel : ViewModel() {

    private val TAG = "LibraryViewModel"
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    private var _userLibrary = MutableLiveData<List<FullMovie>>().apply {
        value = listOf()
    }
    var userLibrary: LiveData<List<FullMovie>> = _userLibrary

    fun fetchUserLibrary() {
        db.collection("Users")
            .document(auth.currentUser!!.uid)
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e(TAG, "Could not find user : ${firebaseFirestoreException.code} / ${firebaseFirestoreException.message}")
                } else {
                    documentSnapshot?.let {
                        if (it.exists()) {
                            val user = documentSnapshot.toObject(User::class.java)
                            _userLibrary.value = user?.movies ?: listOf()
                        }
                    }
                }
            }
    }

    fun getMovie(at: Int): FullMovie? {
        return userLibrary.value?.get(at)
    }
}

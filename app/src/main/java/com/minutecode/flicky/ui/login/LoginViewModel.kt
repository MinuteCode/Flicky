package com.minutecode.flicky.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.minutecode.flicky.model.user.User

class LoginViewModel: ViewModel() {

    private val TAG = "LoginViewModel"

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore

    private lateinit var listener: LoginActivityListener

    fun registerUser(email: String, password: String) {
        firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Firebase.firestore.collection("Users")
                        .document(task.result!!.user!!.uid)
                        .set(User(task.result!!.user!!.uid, setOf(), null, null, null))
                    listener.onRegisterSuccess(task.result!!.user!!)
                } else {
                    listener.onRegisterFailure(task.exception!!)
                    Log.e(TAG, task.exception?.message ?: "User creation no exception message")
                }
            }
    }

    fun login() {
        firebaseAuth.currentUser?.let { user ->
            firestore.collection("Users")
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.exists()) {
                        addUserToDB(User(user.uid, setOf(), null, null, null))
                    } else {
                        listener.onLoginSuccess()
                        Log.i(TAG, "User found in the DB")
                    }
                }
            return
        }
        throw LoginException("User not authenticated")
    }

    fun login(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    listener.onLoginSuccess()
                } else {
                    throw LoginException(message = it.exception!!.message!!)
                }
            }
    }

    private fun addUserToDB(user: User) {
        firestore.collection("Users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .set(user.storeFormat)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    listener.onUserAddedToDB()
                } else {
                    listener.onUserAdditionFailure(it.exception!!)
                    throw LoginException("User addition to DB unsuccessful : ${it.exception!!.message}")
                }
            }
    }

    fun setListener(listener: LoginActivityListener) {
        this.listener = listener
    }
}

interface LoginActivityListener {
    fun onRegisterSuccess(user: FirebaseUser)
    fun onRegisterFailure(exception: Exception)
    fun onUserAddedToDB()
    fun onUserAdditionFailure(e: Exception)
    fun onLoginSuccess()
}
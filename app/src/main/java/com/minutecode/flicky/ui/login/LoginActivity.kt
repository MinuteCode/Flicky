package com.minutecode.flicky.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.minutecode.flicky.MainActivity
import com.minutecode.flicky.R
import com.minutecode.flicky.model.user.User

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"
    private lateinit var firebaseAuth: FirebaseAuth
    private val firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        val connectButton: Button = findViewById(R.id.login_button)
        val registerButton: Button = findViewById(R.id.register_button)
        val emailEditText: TextInputEditText = findViewById(R.id.email_field)
        val passwordEditText: TextInputEditText = findViewById(R.id.password_field)

        connectButton.setOnClickListener {
            startMainActivity()
        }

        registerButton.setOnClickListener {
            try {
                checkLoginCredentials(emailEditText)
                checkLoginCredentials(passwordEditText)
                firebaseAuth
                    .createUserWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Firebase.firestore.collection("Users")
                                .document(task.result!!.user!!.uid)
                                .set(User(task.result!!.user!!.uid, setOf(), null, null, null))
                            startMainActivity()
                        } else {
                            Snackbar
                                .make(
                                    findViewById(R.id.login_main_view),
                                    "User creation failed : ${task.exception?.message ?: "No exception message"}",
                                    Snackbar.LENGTH_SHORT)
                                .show()
                            Log.e(TAG, task.exception?.message ?: "User creation no exception message")
                        }
                    }
            } catch (e : LoginException) {
                Snackbar.make(
                    findViewById(R.id.login_main_view),
                    e.message ?: "Credentials exception no message",
                    Snackbar.LENGTH_SHORT
                ).show()

                Log.e(TAG, e.message ?: "Credentials exception no message")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.currentUser?.let { user ->
            firestore.collection("Users")
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.exists()) {
                        firestore.collection("Users")
                            .document(FirebaseAuth.getInstance().currentUser!!.uid)
                            .set(
                                User(
                                    user.uid,
                                    setOf(),
                                    user.displayName,
                                    user.email,
                                    user.phoneNumber
                                ).storeFormat)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    startMainActivity()
                                }
                            }
                    } else {
                        startMainActivity()
                    }
                }
        }
    }

    private fun startMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java)
        finishAffinity()
        startActivity(mainIntent)
    }

    private fun checkLoginCredentials(textInputEditText: TextInputEditText) {
        if (textInputEditText.text?.isEmpty() == true) {
            throw LoginException("login credentials input empty")
        }
    }
}

class LoginException(message: String): Exception(message)

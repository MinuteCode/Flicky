package com.minutecode.flicky.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.minutecode.flicky.MainActivity
import com.minutecode.flicky.R

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"
    private lateinit var firebaseAuth: FirebaseAuth

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
        firebaseAuth.currentUser?.let {
            startMainActivity()
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

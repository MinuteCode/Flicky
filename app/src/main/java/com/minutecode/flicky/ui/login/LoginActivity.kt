package com.minutecode.flicky.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseUser
import com.minutecode.flicky.MainActivity
import com.minutecode.flicky.R

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"

    private lateinit var viewModel: LoginViewModel

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        viewModel.setListener(object: LoginActivityListener {
            override fun onRegisterSuccess(user: FirebaseUser) {
                startMainActivity()
            }

            override fun onRegisterFailure(exception: Exception) {
                Snackbar
                    .make(
                        findViewById(R.id.login_main_view), "User creation failed : ${exception.message ?: "No exception message"}", Snackbar.LENGTH_SHORT)
                    .show()
            }

            override fun onUserAddedToDB() {
                Snackbar
                    .make(
                        findViewById(R.id.login_main_view), "User added to DB, Welcome !", Snackbar.LENGTH_SHORT)
                    .show()
            }

            override fun onUserAdditionFailure(e: Exception) {
                Log.e(TAG, e.message)
            }

            override fun onLoginSuccess() {
                startMainActivity()
            }
        })

        val connectButton: Button = findViewById(R.id.login_button)
        val registerButton: Button = findViewById(R.id.register_button)
        emailEditText = findViewById(R.id.email_field)
        passwordEditText = findViewById(R.id.password_field)

        connectButton.setOnClickListener {
            try {
                checkLoginCredentials(emailEditText)
                checkLoginCredentials(passwordEditText)
                viewModel.login(emailEditText.text.toString(), passwordEditText.text.toString())
            } catch (e: LoginException) {
                Snackbar
                    .make(findViewById(R.id.login_main_view), e.message!!, Snackbar.LENGTH_LONG)
                    .show()
            }
        }

        registerButton.setOnClickListener {
            try {
                checkLoginCredentials(emailEditText)
                checkLoginCredentials(passwordEditText)
                viewModel.registerUser(emailEditText.text.toString(), passwordEditText.text.toString())
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
        try {
            viewModel.login()
        } catch (e: LoginException) {
            Snackbar
                .make(findViewById(R.id.login_main_view), e.message!!, Snackbar.LENGTH_LONG)
                .show()
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

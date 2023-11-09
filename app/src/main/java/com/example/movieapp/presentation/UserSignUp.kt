package com.example.movieapp.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.movieapp.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

class UserSignUp : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var passwordRetyped: EditText
    private lateinit var createAccountButton: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_layout)

        email = findViewById(R.id.emailField)
        password = findViewById(R.id.passwordField)
        passwordRetyped = findViewById(R.id.retypePasswordField)
        createAccountButton = findViewById(R.id.buttonCreateLogin)

        // Connecting to Firebase:
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this@UserSignUp)
        createAccountButton.setOnClickListener {
            val enteredUsername: String = email.text.toString()
            val enteredPassword: String = password.text.toString()
            val enteredPasswordRetyped: String = passwordRetyped.text.toString()

            // Verify that both passwords are equal:
            if (enteredPassword.equals(enteredPasswordRetyped, true)) {
                firebaseAuth
                    .createUserWithEmailAndPassword(enteredUsername, enteredPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = firebaseAuth.currentUser
                            val toastMsg = "Sign up success as " + user!!.email
                            Toast.makeText(this@UserSignUp, toastMsg, Toast.LENGTH_LONG).show()
                            firebaseAnalytics.logEvent("SignUp_Success", null)

                            // Navigate to home screen:
                            val intent = Intent(this@UserSignUp, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            val exception: Exception? = task.exception
                            val toastMsg = "Sign up failed!"

                            Toast.makeText(this@UserSignUp, toastMsg, Toast.LENGTH_LONG).show()
                            firebaseAnalytics.logEvent("SignUp_Failed: $exception", null)
                        }
                    }
            }
        }
    }
}
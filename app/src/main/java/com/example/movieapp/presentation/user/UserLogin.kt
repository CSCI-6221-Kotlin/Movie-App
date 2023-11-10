package com.example.movieapp.presentation.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.movieapp.R
import com.example.movieapp.presentation.MainActivity
import com.google.firebase.auth.FirebaseAuth

class UserLogin : AppCompatActivity() {
    private lateinit var createAccountButton: Button
    private lateinit var loginButton: Button
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)

        email = findViewById(R.id.emailLoginField)
        password = findViewById(R.id.passwordLoginField)

        // Connect to Firebase:
        firebaseAuth = FirebaseAuth.getInstance()

        // Login button clicked:
        loginButton = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            val enteredEmail: String = email.text.toString()
            val enteredPassword: String = password.text.toString()

            if (enteredEmail.isEmpty() || enteredPassword.isEmpty()) {
                Toast.makeText(this@UserLogin, "Please type-in both your email and password!", Toast.LENGTH_SHORT).show()
            } else {
                firebaseAuth
                    .signInWithEmailAndPassword(enteredEmail, enteredPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("Testing", "Successful")
                            // Navigate to home screen:
                            val intent = Intent(this@UserLogin, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@UserLogin, "Login has failed!", Toast.LENGTH_SHORT).show()
                            Log.d("Testing", "Unsuccessful!")
                        }
                    }
            }
        }

        // Create Account button clicked navigates to the signup screen:
        createAccountButton = findViewById(R.id.createAccountButton)
        createAccountButton.setOnClickListener {
            val intent = Intent(this@UserLogin, UserSignUp::class.java)
            startActivity(intent)
        }
    }
}
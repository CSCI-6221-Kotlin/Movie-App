package com.example.movieapp.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.movieapp.R

class UserLogin : AppCompatActivity() {
    private lateinit var createAccountButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)

        // Create Account Button selected navigates to the signup screen:
        createAccountButton = findViewById(R.id.createAccountButton)
        createAccountButton.setOnClickListener {
            val intent = Intent(this@UserLogin, UserSignUp::class.java)
            startActivity(intent)
        }
    }
}
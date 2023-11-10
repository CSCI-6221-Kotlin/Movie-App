package com.example.movieapp.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.movieapp.R
import com.google.firebase.auth.FirebaseAuth

class UserAccount : AppCompatActivity() {
    private lateinit var logoutButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_layout)

        // Logout button clicked:
        logoutButton = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            if (FirebaseAuth.getInstance().currentUser != null) {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this@UserAccount, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
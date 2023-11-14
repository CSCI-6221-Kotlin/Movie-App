package com.example.movieapp.presentation.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.movieapp.R
import com.example.movieapp.firebase.UserDatabase
import com.example.movieapp.presentation.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserAccount : AppCompatActivity() {
    private lateinit var logoutButton: ImageButton
    private lateinit var usernameText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_layout)

        // Connect to Firebase:
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseDatabase = FirebaseDatabase.getInstance()

        // Change username text:
        changeUsernameText(firebaseAuth, firebaseDatabase)

        // Logout button clicked:
        logoutButton = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Log out of your account?")
                .setPositiveButton("Log Out") { _, _ ->
                    if (firebaseAuth.currentUser != null) {
                        firebaseAuth.signOut()

                        val intent = Intent(this@UserAccount, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun changeUsernameText(firebaseAuth: FirebaseAuth, firebaseDatabase: FirebaseDatabase) {
        // Change username field to current user's username:
        usernameText = findViewById(R.id.usernameText)
        var currentUsername: String
        val currentUserEmail = firebaseAuth.currentUser?.email
        if (currentUserEmail != null) {
            val referenceFirebaseData = firebaseDatabase.getReference("Users")
            referenceFirebaseData.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { childSnapshot: DataSnapshot ->
                        val u: UserDatabase? = childSnapshot.getValue(UserDatabase::class.java)
                        if (u != null) {
                            if (u.email.equals(currentUserEmail, true)) {
                                currentUsername = u.username
                                usernameText.text = currentUsername
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("MovieApp", "Unable to change username text!")
                }
            })
        }
    }
}
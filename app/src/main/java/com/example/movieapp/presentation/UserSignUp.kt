package com.example.movieapp.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.movieapp.R
import com.example.movieapp.firebase.UserDatabase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserSignUp : AppCompatActivity() {
    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var passwordRetyped: EditText
    private lateinit var createAccountButton: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_layout)

        username = findViewById(R.id.usernameField)
        email = findViewById(R.id.emailField)
        password = findViewById(R.id.passwordField)
        passwordRetyped = findViewById(R.id.retypePasswordField)
        createAccountButton = findViewById(R.id.buttonCreateLogin)

        // Connecting to Firebase:
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this@UserSignUp)
        firebaseDatabase = FirebaseDatabase.getInstance()
        createAccountButton.setOnClickListener {
        val enteredUsername: String = username.text.toString()
        val enteredEmail: String = email.text.toString()
        val enteredEmailDB = enteredEmail.replace(".", "")
        val enteredPassword: String = password.text.toString()
        val enteredPasswordRetyped: String = passwordRetyped.text.toString()

        // Verify that both passwords are equal:
        if (enteredPassword.equals(enteredPasswordRetyped, true)) {
            firebaseAuth
                .createUserWithEmailAndPassword(enteredEmail, enteredPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Store email and password:
                        val user = firebaseAuth.currentUser
                        val toastMsg = "Sign up success as " + user!!.email
                        Toast.makeText(this@UserSignUp, toastMsg, Toast.LENGTH_LONG).show()
                        firebaseAnalytics.logEvent("SignUp_Success", null)

                        // Store username that'll match to the email:
                        val newUser = UserDatabase(username = enteredUsername)
                        val existingUsers = mutableListOf<UserDatabase>()

                        val referenceFirebaseData = firebaseDatabase.getReference("users/$enteredEmailDB")
                        referenceFirebaseData.addValueEventListener(object: ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                snapshot.children.forEach { childSnapshot: DataSnapshot ->
                                    val u : UserDatabase? = childSnapshot.getValue(UserDatabase::class.java)
                                    if (u != null) {
                                        if (!existingUsers.contains(u)) {
                                            existingUsers.add(u)
                                        }
                                    }
                                }

                                if (!existingUsers.contains(newUser)) {
                                    val referenceFirebase = firebaseDatabase.getReference("Users/$enteredEmailDB")
                                    referenceFirebase.push().setValue(newUser)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(this@UserSignUp, "Failed to connect to database!", Toast.LENGTH_LONG).show()
                            }
                        })

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
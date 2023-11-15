package com.example.movieapp.presentation.user

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.movieapp.R
import com.example.movieapp.firebase.UserDatabase
import com.example.movieapp.presentation.MainActivity
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
    private lateinit var createProgressBar: ProgressBar

    private val textWatcher: TextWatcher = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Verify if all components are filled out:
            val usernameFilled: Boolean = username.text.toString().isNotEmpty()
            val emailFilled: Boolean = email.text.toString().isNotEmpty()
            val passwordFilled: Boolean = password.text.toString().isNotEmpty()
            val passwordRetypedFilled: Boolean = passwordRetyped.text.toString().isNotEmpty()

            if (usernameFilled && emailFilled) {
                createAccountButton.isEnabled = passwordFilled && passwordRetypedFilled
            }
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_layout)

        username = findViewById(R.id.usernameField)
        username.addTextChangedListener(textWatcher)
        email = findViewById(R.id.emailField)
        email.addTextChangedListener(textWatcher)
        password = findViewById(R.id.passwordField)
        password.addTextChangedListener(textWatcher)
        passwordRetyped = findViewById(R.id.retypePasswordField)
        passwordRetyped.addTextChangedListener(textWatcher)
        createAccountButton = findViewById(R.id.buttonCreateLogin)

        // Make sure progress bar is invisible:
        createProgressBar = findViewById(R.id.createProgressBar)
        createProgressBar.visibility = View.INVISIBLE

        createAccountButton.setOnClickListener {
            val enteredUsername: String = username.text.toString()
            val enteredEmail: String = email.text.toString()
            val enteredPassword: String = password.text.toString()
            val enteredPasswordRetyped: String = passwordRetyped.text.toString()

            // Make progress bar visible:
            createProgressBar.visibility = View.VISIBLE

            // Verify that the username is unique:
            // If unique: It'll create the account only if email was not used previously
            // and passwords are the same
            verifyUniqueUsername(enteredUsername, enteredEmail, enteredPassword, enteredPasswordRetyped)
        }
    }

    private fun verifyUniqueUsername (enteredUsername: String, enteredEmail : String, enteredPassword: String, enteredPasswordRetyped: String) {
        var usernameUnique = true
        if (enteredUsername.isNotEmpty()) {
            val firebaseDatabase : FirebaseDatabase = FirebaseDatabase.getInstance()
            val referenceFirebaseData = firebaseDatabase.getReference("Users")
            referenceFirebaseData.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { childSnapShot: DataSnapshot ->
                        val u : UserDatabase? = childSnapShot.getValue(UserDatabase::class.java)
                        if (u != null) {
                            if (u.username.equals(enteredUsername, true)) {
                                usernameUnique = false
                            }
                        }
                    }

                    if (usernameUnique) {
                        if (verifyPasswords(enteredPassword, enteredPasswordRetyped)) {
                            createAccount(enteredEmail, enteredPassword, enteredUsername)
                            referenceFirebaseData.removeEventListener(this)
                        } else {
                            Toast.makeText(applicationContext, "Passwords are not the same. Please type again.", Toast.LENGTH_SHORT).show()
                            createProgressBar.visibility = View.INVISIBLE
                            referenceFirebaseData.removeEventListener(this)
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "$enteredUsername is already taken. Please type a different username.",
                            Toast.LENGTH_SHORT
                        ).show()
                        createProgressBar.visibility = View.INVISIBLE
                        referenceFirebaseData.removeEventListener(this)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("MovieApp", "Unable to identify usernames")
                }
            })
        }
    }

    private fun verifyPasswords(enteredPassword : String, enteredPasswordRetyped : String): Boolean {
        return enteredPassword.equals(enteredPasswordRetyped, true)
    }

    private fun createAccount(enteredEmail : String, enteredPassword : String, enteredUsername: String) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val existingUsers = mutableListOf<UserDatabase>()

        firebaseAuth
            .createUserWithEmailAndPassword(enteredEmail, enteredPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val newUser = UserDatabase(username = enteredUsername, email = enteredEmail)
                    val user = firebaseAuth.currentUser

                    // Store username in DB that'll match to the email:
                    val referenceFirebaseData = firebaseDatabase.getReference("Users")
                    referenceFirebaseData.addValueEventListener(object : ValueEventListener {
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
                                referenceFirebaseData.push().setValue(newUser)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(
                                this@UserSignUp,
                                "Failed to connect to database!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })

                    // Make sure progress bar is invisible:
                    createProgressBar.visibility = View.INVISIBLE

                    // Navigate to home screen:
                    val intent = Intent(this@UserSignUp, MainActivity::class.java)
                    startActivity(intent)

                    val toastMsg = "Sign up success as " + user!!.email
                    Toast.makeText(this@UserSignUp, toastMsg, Toast.LENGTH_SHORT).show()
                } else {
                    // Make sure progress bar is invisible:
                    createProgressBar.visibility = View.INVISIBLE

                    Toast.makeText(this@UserSignUp, "Creating account has failed with provided email and password!", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
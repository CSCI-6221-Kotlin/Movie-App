package com.example.movieapp.presentation.user

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.movieapp.R
import com.example.movieapp.firebase.UserDatabase
import com.example.movieapp.presentation.MainActivity
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
    private lateinit var createProgressBar: ProgressBar
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var firebaseDatabase: FirebaseDatabase

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

            // Make progress bar visible:
            createProgressBar.visibility = View.VISIBLE

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

                            val referenceFirebaseData =
                                firebaseDatabase.getReference("users/$enteredEmailDB")
                            referenceFirebaseData.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    snapshot.children.forEach { childSnapshot: DataSnapshot ->
                                        val u: UserDatabase? =
                                            childSnapshot.getValue(UserDatabase::class.java)
                                        if (u != null) {
                                            if (!existingUsers.contains(u)) {
                                                existingUsers.add(u)
                                            }
                                        }
                                    }

                                    if (!existingUsers.contains(newUser)) {
                                        val referenceFirebase =
                                            firebaseDatabase.getReference("Users/$enteredEmailDB")
                                        referenceFirebase.push().setValue(newUser)
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(
                                        this@UserSignUp,
                                        "Failed to connect to database!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            })

                            // Make sure progress bar is invisible now:
                            createProgressBar.visibility = View.INVISIBLE

                            // Navigate to home screen:
                            val intent = Intent(this@UserSignUp, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            val exception: Exception? = task.exception
                            val toastMsg = "Sign up failed!"

                            Toast.makeText(this@UserSignUp, toastMsg, Toast.LENGTH_LONG).show()
                            firebaseAnalytics.logEvent("SignUp_Failed: $exception", null)

                            // Make sure progress bar is invisible now:
                            createProgressBar.visibility = View.INVISIBLE
                        }
                    }
            } else {
                Toast.makeText(this@UserSignUp, "Please make sure passwords are the same!", Toast.LENGTH_SHORT).show()

                // Make sure progress bar is invisible now:
                createProgressBar.visibility = View.INVISIBLE
            }
        }
    }
}
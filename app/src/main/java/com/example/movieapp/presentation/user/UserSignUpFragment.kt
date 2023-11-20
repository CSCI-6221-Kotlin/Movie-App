package com.example.movieapp.presentation.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.movieapp.databinding.FragmentUserSignUpBinding
import com.example.movieapp.firebase.UserDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class UserSignUpFragment : Fragment() {

    private var _binding: FragmentUserSignUpBinding? = null
    private val binding get() = _binding!!

    private val textWatcher: TextWatcher = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Verify if all components are filled out:
            val usernameFilled: Boolean = binding.usernameField.text.toString().isNotEmpty()
            val emailFilled: Boolean = binding.emailField.text.toString().isNotEmpty()
            val passwordFilled: Boolean = binding.passwordField.text.toString().isNotEmpty()
            val passwordRetypedFilled: Boolean = binding.retypePasswordField.text.toString().isNotEmpty()

            if (usernameFilled && emailFilled) {
                binding.buttonCreateLogin.isEnabled = passwordFilled && passwordRetypedFilled
            }
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUserSignUpBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners(){
        binding.apply {
            usernameField.addTextChangedListener(textWatcher)
            emailField.addTextChangedListener(textWatcher)
            passwordField.addTextChangedListener(textWatcher)
            retypePasswordField.addTextChangedListener(textWatcher)
            // Make sure progress bar is invisible:
            createProgressBar.visibility = View.INVISIBLE
            buttonCreateLogin.setOnClickListener {
                val enteredUsername: String = usernameField.text.toString()
                val enteredEmail: String = emailField.text.toString()
                val enteredPassword: String = passwordField.text.toString()
                val enteredPasswordRetyped: String = retypePasswordField.text.toString()

                // Make progress bar visible:
                createProgressBar.visibility = View.VISIBLE

                // Verify that the username is unique:
                // If unique: It'll create the account only if email was not used previously
                // and passwords are the same
                verifyUniqueUsername(enteredUsername, enteredEmail, enteredPassword, enteredPasswordRetyped)
            }
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
                            Toast.makeText(requireContext(), "Passwords are not the same. Please type again.", Toast.LENGTH_SHORT).show()
                            binding.createProgressBar.visibility = View.INVISIBLE
                            referenceFirebaseData.removeEventListener(this)
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "$enteredUsername is already taken. Please type a different username.",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.createProgressBar.visibility = View.INVISIBLE
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
                                requireContext(),
                                "Failed to connect to database!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })

                    // Make sure progress bar is invisible:
                    binding.createProgressBar.visibility = View.INVISIBLE

                    // TODO Navigate to moviesfragment
                    val action = UserSignUpFragmentDirections.actionUserSignUpFragmentToMoviesNavGraph()
                    findNavController().navigate(action)

                    val toastMsg = "Sign up success as " + user!!.email
                    Toast.makeText(requireContext(), toastMsg, Toast.LENGTH_SHORT).show()
                } else {
                    // Make sure progress bar is invisible:
                    binding.createProgressBar.visibility = View.INVISIBLE

                    Toast.makeText(requireContext(), "Creating account has failed with provided email and password!", Toast.LENGTH_SHORT).show()
                }
            }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
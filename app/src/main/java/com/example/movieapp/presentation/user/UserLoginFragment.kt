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
import com.example.movieapp.databinding.FragmentUserLoginBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserLoginFragment : Fragment() {

    private var _binding:FragmentUserLoginBinding? = null
    private val binding get() = _binding!!
    private  var firebaseAuth = FirebaseAuth.getInstance()

    private val textWatcher: TextWatcher = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Verify if email and password is filled out to enable the login button:
            val emailFilled: Boolean = binding.emailLoginField.text.toString().isNotEmpty()
            val passwordFilled: Boolean = binding.passwordLoginField.text.toString().isNotEmpty()

            binding.loginButton.isEnabled = emailFilled && passwordFilled
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUserLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners(){
        binding.emailLoginField.addTextChangedListener(textWatcher)
        binding.passwordLoginField.addTextChangedListener(textWatcher)

        binding.loginButton.setOnClickListener {
            val enteredEmail: String = binding.emailLoginField.text.toString()
            val enteredPassword: String = binding.passwordLoginField.text.toString()

            if (enteredEmail.isEmpty() || enteredPassword.isEmpty()) {
                Toast.makeText(context, "Please type-in both your email and password!", Toast.LENGTH_SHORT).show()
            }

            else {
                firebaseAuth
                    .signInWithEmailAndPassword(enteredEmail, enteredPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("Testing", "Successful")
                            // Navigate to home screen:
                            val action = UserLoginFragmentDirections.actionUserLoginFragmentToMoviesNavGraph()
                            findNavController().navigate(action)
                        } else {
                            Toast.makeText(context, "Login has failed!", Toast.LENGTH_SHORT).show()
                            Log.d("Testing", "Unsuccessful!")
                        }
                    }
            }
        }

        binding.createAccountButton.setOnClickListener {
            //TODO Navigate to UserSignUp
            val action = UserLoginFragmentDirections.actionUserLoginFragmentToUserSignUpFragment()
            findNavController().navigate(action)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

}
}
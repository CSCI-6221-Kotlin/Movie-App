package com.example.movieapp.presentation.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentUserProfileBinding
import com.example.movieapp.domain.model.MovieInfo
import com.example.movieapp.domain.preferences.Preferences
import com.example.movieapp.presentation.home.MovieListAdapter
import com.example.movieapp.presentation.movieDetail.MovieDetailFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var favoriteMoviesAdapter : MovieListAdapter

    private val userProfileViewModel : UserProfileViewModel by viewModels()

    @Inject
    lateinit var preferences: Preferences

    // Connect to Firebase:
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUserProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userProfileViewModel.getFavoriteMovies()
        initViews()
        initListeners()
        initObservers()
    }

    private fun initViews(){
        binding.usernameText.text = preferences.getUserPrefs().username
        favoriteMoviesAdapter = MovieListAdapter()
        binding.favoriteMoviesRV.apply {
            adapter = favoriteMoviesAdapter
            layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
        }
    }

    private fun initListeners(){
        binding.logoutButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Log out of your account?")
                .setPositiveButton("Log Out") { _, _ ->
                    if (firebaseAuth.currentUser != null) {
                        firebaseAuth.signOut()
                        //Clear userName and email
                        preferences.saveUserName("")
                        preferences.saveEmail("")
                        findNavController().popBackStack(R.id.userLoginFragment,false)
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
        val listener: (MovieInfo) -> Unit = { movieInfo ->
            val action = UserProfileFragmentDirections.actionUserProfileFragmentToMovieDetailFragment(movieInfo.id)
            findNavController().navigate(action)
        }
        favoriteMoviesAdapter.setOnMovieClickListener(listener)
    }

    private fun initObservers(){
        viewLifecycleOwner.lifecycleScope.launch {
           userProfileViewModel.favoriteMovies.collect{ favoriteMovies ->
               favoriteMovies?.let {
                   if(it.isNotEmpty()){
                       binding.favoriteMoviesView.visibility = View.VISIBLE
                       favoriteMoviesAdapter.differ.submitList(it)
                   }
               }
           }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
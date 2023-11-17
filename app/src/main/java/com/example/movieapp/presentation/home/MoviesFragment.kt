package com.example.movieapp.presentation.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentMoviesBinding
import com.example.movieapp.domain.model.MovieInfo
import com.example.movieapp.domain.preferences.Preferences
import com.example.movieapp.domain.usecase.MovieDisplayType
import com.example.movieapp.firebase.UserDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MoviesFragment : Fragment() {

    private var _binding: FragmentMoviesBinding? = null
    private lateinit var movieListAdapter: MovieListAdapter
    private lateinit var movieListAdapter2: MovieListAdapter
    private lateinit var movieListAdapter3: MovieListAdapter
    private lateinit var movieListAdapter4: MovieListAdapter

    private val binding get() = _binding!!

    @Inject
    lateinit var preferences: Preferences

    private val moviesViewModel: MoviesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
        getMovies()
        initObservers()

        //Only make firebase call for the first time
        if (preferences.getUserPrefs().username.isEmpty()) {
            changeWelcomeText()
        }
    }

    private fun getMovies() {
        moviesViewModel.fetchMovies()
    }

    private fun initViews() {
        movieListAdapter = MovieListAdapter()
        movieListAdapter2 = MovieListAdapter()
        movieListAdapter3 = MovieListAdapter()
        movieListAdapter4 = MovieListAdapter()

        binding.recyclerView1.apply {
            adapter = movieListAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.recyclerView2.apply {
            adapter = movieListAdapter2
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.recyclerView3.apply {
            adapter = movieListAdapter3
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.recyclerView4.apply {
            adapter = movieListAdapter4
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch() {
            moviesViewModel.moviesListUIState.collect {
                it?.let { movieListUI ->
                    movieListUI.movieList.map { movieInfo ->
                        when (movieInfo.movieDisplayType) {
                            MovieDisplayType.TRENDING -> {
                                movieListAdapter.differ.submitList(movieListUI.movieList)
                            }

                            MovieDisplayType.POPULAR -> {
                                movieListAdapter2.differ.submitList(movieListUI.movieList)
                            }

                            MovieDisplayType.TOP_RATED -> {
                                movieListAdapter3.differ.submitList(movieListUI.movieList)
                            }

                            MovieDisplayType.UPCOMING -> {
                                movieListAdapter4.differ.submitList(movieListUI.movieList)
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private fun changeWelcomeText() {
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()

        var currentUsername: String
        val currentUserEmail = firebaseAuth.currentUser?.email

        val welcomeTextView: TextView? = view?.findViewById(R.id.welcomeText)
        if (currentUserEmail != null) {
            val referenceFirebaseData = firebaseDatabase.getReference("Users")
            referenceFirebaseData.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { childSnapshot: DataSnapshot ->
                        val u: UserDatabase? = childSnapshot.getValue(UserDatabase::class.java)
                        if (u != null) {
                            if (u.email.equals(currentUserEmail, true)) {
                                //Save user name and email to preferences
                                currentUsername = u.username
                                preferences.saveUserName(currentUsername)
                                preferences.saveEmail(currentUserEmail)

                                if (welcomeTextView != null) {
                                    welcomeTextView.text =
                                        getString(R.string.welcomeUser, currentUsername)
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("MovieApp", "Unable to change welcome text!")
                }
            })
        } else {
            if (welcomeTextView != null) {
                welcomeTextView.text = getString(R.string.movie)
            }
        }
    }

    private fun initListeners() {
        val listener: (MovieInfo) -> Unit = { movieInfo ->
            val action =
                MoviesFragmentDirections.actionMoviesFragmentToMovieDetailFragment(movieInfo.id)
            findNavController().navigate(action)
        }

        movieListAdapter.setOnMovieClickListener(listener)
        movieListAdapter2.setOnMovieClickListener(listener)
        movieListAdapter3.setOnMovieClickListener(listener)
        movieListAdapter4.setOnMovieClickListener(listener)

        // User Profile Icon Clicked:
        binding.profileImageButton.setOnClickListener { _ ->
            // Check if a user is logged in:
            val action = MoviesFragmentDirections.actionMoviesFragmentToUserProfileFragment()
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        if (preferences.getUserPrefs().username.isNotEmpty()) {
            binding.welcomeText.text =
                getString(R.string.welcomeUser, preferences.getUserPrefs().username)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

package com.example.movieapp.presentation.home

import android.content.Intent
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
import com.example.movieapp.domain.usecase.MovieDisplayType
import com.example.movieapp.firebase.UserDatabase
import com.example.movieapp.presentation.user.UserAccount
import com.example.movieapp.presentation.user.UserLogin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MoviesFragment : Fragment() {

    private var _binding: FragmentMoviesBinding? = null
    private lateinit var movieListAdapter: MovieListAdapter
    private lateinit var movieListAdapter2: MovieListAdapter
    private lateinit var movieListAdapter3: MovieListAdapter
    private lateinit var movieListAdapter4: MovieListAdapter

    private val binding get() = _binding!!

    private val moviesViewModel: MoviesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMoviesBinding.inflate(inflater,container,false)

        // User Profile Icon Clicked:
        binding.profileImageButton.setOnClickListener { v ->
            // Check if a user is logged in:
            if (FirebaseAuth.getInstance().currentUser == null) {
                val intent = Intent(v.context, UserLogin::class.java)
                v.context.startActivity(intent)
            } else {
                val intent = Intent(v.context, UserAccount::class.java)
                v.context.startActivity(intent)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
        getMovies()
        initObservers()
        changeWelcomeText()
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

        val welcomeT: TextView? = view?.findViewById(R.id.welcomeText)
        if (currentUserEmail != null) {
            val currentUserEmailDB = currentUserEmail.replace(".", "")

            val referenceFirebaseData = firebaseDatabase.getReference("Users/$currentUserEmailDB")
            referenceFirebaseData.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { childSnapshot: DataSnapshot ->
                        val u: UserDatabase? = childSnapshot.getValue(UserDatabase::class.java)
                        if (u != null) {
                            currentUsername = u.username

                            if (welcomeT != null) {
                                welcomeT.text = getString(R.string.welcomeUser, currentUsername)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("MovieApp", "Unable to change welcome text!")
                }
            })
        }
    }

    private fun initListeners() {
        val listener: (MovieInfo) -> Unit = { movieInfo ->
            val action = MoviesFragmentDirections.actionMoviesFragmentToMovieDetailFragment(movieInfo.id)
            findNavController().navigate(action)
        }

        movieListAdapter.setOnMovieClickListener(listener)
        movieListAdapter2.setOnMovieClickListener(listener)
        movieListAdapter3.setOnMovieClickListener(listener)
        movieListAdapter4.setOnMovieClickListener(listener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

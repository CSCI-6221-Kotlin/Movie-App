package com.example.movieapp.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.databinding.FragmentMoviesBinding
import com.example.movieapp.domain.usecase.MovieDisplayType
import com.google.firebase.auth.FirebaseAuth
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

    private val moviesViewModel:MoviesViewModel by viewModels()

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
        initAdapters()
        getMovies()
        initListeners()
    }

    private fun getMovies(){
        moviesViewModel.fetchMovies()
    }

    private fun initAdapters(){
        movieListAdapter = MovieListAdapter()
        movieListAdapter2 = MovieListAdapter()
        movieListAdapter3 = MovieListAdapter()
        movieListAdapter4 = MovieListAdapter()

        binding.recyclerView1.apply {
            adapter = movieListAdapter
            layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
        }

        binding.recyclerView2.apply {
            adapter = movieListAdapter2
            layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
        }

        binding.recyclerView3.apply {
            adapter = movieListAdapter3
            layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
        }

        binding.recyclerView4.apply {
            adapter = movieListAdapter4
            layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
        }
    }

    private fun initListeners(){
        viewLifecycleOwner.lifecycleScope.launch() {
            moviesViewModel.moviesListUIState.collect{
                it?.let { movieListUI ->
                    movieListUI.movieList.map { movieInfo->
                        when(movieInfo.movieDisplayType){
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
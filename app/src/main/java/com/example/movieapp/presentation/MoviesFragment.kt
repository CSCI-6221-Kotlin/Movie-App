package com.example.movieapp.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.databinding.FragmentMoviesBinding
import com.example.movieapp.domain.model.MovieInfo


class MoviesFragment : Fragment() {

    private var _binding: FragmentMoviesBinding? = null
    private lateinit var movieListAdapter: MovieListAdapter

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMoviesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
    }

    private fun initAdapter(){
        movieListAdapter = MovieListAdapter()
        movieListAdapter.differ.submitList(listOf(
            MovieInfo(1,"PP1","MOVIE1"),
            MovieInfo(2,"PP2","MOVIE2"),
            MovieInfo(3,"PP3","MOVIE3"),
            MovieInfo(4,"PP4","MOVIE4"),
            MovieInfo(5,"PP5","MOVIE5"),
        ))
        binding.recyclerView1.apply {
            adapter = movieListAdapter
            layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
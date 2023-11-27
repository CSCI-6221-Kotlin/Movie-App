package com.example.movieapp.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.core.toSafeString
import com.example.movieapp.databinding.FragmentMovieSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MovieSearchFragment : Fragment() {

    private var _binding: FragmentMovieSearchBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel: SearchViewModel by viewModels()

    private lateinit var movieSearchListAdapter: MovieSearchListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMovieSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.movieSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchViewModel.searchMovie(query?.trim().toSafeString())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText.isNullOrBlank()){
                    clearSearchResults()
                }

                else{
                    lifecycleScope.launch {
                        searchViewModel.searchMovie(newText)
                    }
                }
                return false
            }

        })

        movieSearchListAdapter.setOnMovieClickListener {
            val action = MovieSearchFragmentDirections.actionMovieSearchFragmentToMovieDetailFragment(it.id,it.releaseDate)
            findNavController().navigate(action)
        }
    }

    private fun initViews() {
        movieSearchListAdapter = MovieSearchListAdapter()

        binding.movieSearchView.apply {
            requestFocus()
        }

        binding.searchListRV.apply {
            adapter = movieSearchListAdapter
            layoutManager = LinearLayoutManager(activity)
        }

    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            searchViewModel.searchMovieListUIState.collect { searchListUI ->
                movieSearchListAdapter.differ.submitList(searchListUI?.movieList)
            }
        }
    }

    private fun clearSearchResults () {
        searchViewModel.clearSearchResults()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
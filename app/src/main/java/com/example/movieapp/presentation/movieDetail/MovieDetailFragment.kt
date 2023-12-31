package com.example.movieapp.presentation.movieDetail

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.core.toSafeString
import com.example.movieapp.databinding.FragmentMovieDetailBinding
import com.example.movieapp.domain.model.MovieDetailUI
import com.example.movieapp.domain.model.MovieInfo
import com.example.movieapp.presentation.home.MovieListAdapter
import com.example.movieapp.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieDetailFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val args: MovieDetailFragmentArgs by navArgs()
    private lateinit var movieRecommendationListAdapter: MovieListAdapter
    private val movieDetailViewModel: MovieDetailViewModel by viewModels()

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
        checkIfMovieIsInFavoriteList()
        getMovieDetail()
        getMovieRecommendation()
        initObservers()
    }

    private fun initViews() {
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        movieRecommendationListAdapter = MovieListAdapter()

        binding.recyclerView4.apply {
            adapter = movieRecommendationListAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun getMovieDetail() {
        movieDetailViewModel.getMovieDetail(args.movieID,args.releaseDate)
    }

    private fun getMovieRecommendation() {
        movieDetailViewModel.getRecommendedMovies(args.movieID)
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            movieDetailViewModel.movieDetailUI.collect {
                updateView(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch() {
            movieDetailViewModel.moviesListUIState.collect {
                it?.let { movieListUI ->
                    if (movieListUI.movieList.isNotEmpty()) {
                        movieRecommendationListAdapter.differ.submitList(movieListUI.movieList)
                        binding.movieCategoryRecommendation.visibility = View.VISIBLE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            movieDetailViewModel.isMovieInFavoriteList.collect { isInFavorites ->
                binding.addToFavorites.isChecked = isInFavorites
            }
        }
    }

    private fun updateView(movieDetailUI: MovieDetailUI?) {
        movieDetailUI?.let {
            binding.apply {
                textTitle.text = it.title
                movieOverview.text = it.overview
                movieReleaseDateValue.text = it.releaseDate
                val percentage = (it.voteAverage * 10).toInt()
                movieVoteAvgText.text = "$percentage"
                circularProgressBar.progress = percentage

                if (movieDetailViewModel.movieDetailUI.value?.homepage?.isNotBlank() == true) {
                    homePageText.visibility = View.VISIBLE
                }

                homePageTextValue.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.purple_700
                    )
                )
                homePageTextValue.text = it.homepage

                val content = SpannableString(it.homepage)
                content.setSpan(
                    UnderlineSpan(),
                    0,
                    content.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                homePageTextValue.text = content

                val posterURL = Constants.TMDB_IMAGE_BASE_URL + movieDetailUI.posterPath
                Glide.with(requireContext())
                    .load(posterURL)
                    .centerCrop()
                    .error(R.drawable.ic_launcher_background)
                    .into(movieImageMain)
            }
        }
    }

    private fun initListeners() {
        binding.homePageTextValue.setOnClickListener {
            val homepageUrl = movieDetailViewModel.movieDetailUI.value?.homepage.toSafeString()
            val action =
                MovieDetailFragmentDirections.actionMovieDetailFragmentToMovieWebPageFragment(
                    homepageUrl
                )
            findNavController().navigate(action)
        }

        val listener: (MovieInfo) -> Unit = { movieInfo ->
            val action = MovieDetailFragmentDirections.actionMovieDetailFragmentSelf(movieInfo.id, movieInfo.releaseDate)
            findNavController().navigate(action)
        }


        movieRecommendationListAdapter.setOnMovieClickListener(listener)


        binding.addToFavorites.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                movieDetailViewModel.removeMovieFromFavoriteList(args.movieID)
            }

            else{
                movieDetailViewModel.addMovieToFavoriteList(args.movieID)
            }
        }

    }

    private fun checkIfMovieIsInFavoriteList() {
        movieDetailViewModel.isMovieInFavoriteList(args.movieID)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
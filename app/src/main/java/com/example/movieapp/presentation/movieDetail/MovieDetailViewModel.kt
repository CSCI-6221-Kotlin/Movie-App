package com.example.movieapp.presentation.movieDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.domain.model.MovieDetailUI
import com.example.movieapp.domain.model.MovieListUI
import com.example.movieapp.domain.usecase.GetMovieDetailUseCase
import com.example.movieapp.domain.usecase.GetMovieRecommendationUseCase
import com.example.movieapp.domain.usecase.MovieDisplayType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val getMovieRecommendationUseCase: GetMovieRecommendationUseCase
) : ViewModel(){

    private val _movieDetailUI = MutableStateFlow<MovieDetailUI?>(null)
    val movieDetailUI = _movieDetailUI.asStateFlow()

    private val _moviesListUIState = MutableStateFlow<MovieListUI?>(null)
    val moviesListUIState: StateFlow<MovieListUI?> = _moviesListUIState.asStateFlow()

    fun getMovieDetail(movieID: Int) {
        viewModelScope.launch {
            getMovieDetailUseCase(GetMovieDetailUseCase.Params(movieID))
                .collect {
                    _movieDetailUI.value = it.data
                }
        }
    }

    fun getRecommendedMovies(movieID: Int) {
        viewModelScope.launch {
            getMovieRecommendationUseCase(GetMovieRecommendationUseCase.Params(movieID))
                .collect{
                    _moviesListUIState.value = it.data
                }
        }
    }
}
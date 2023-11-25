package com.example.movieapp.presentation.movieDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.domain.model.MovieDetailUI
import com.example.movieapp.domain.model.MovieListUI
import com.example.movieapp.domain.preferences.Preferences
import com.example.movieapp.domain.usecase.GetMovieDetailUseCase
import com.example.movieapp.domain.usecase.GetMovieRecommendationUseCase
import com.example.movieapp.domain.usecase.InsertFavoriteMovieUseCase
import com.example.movieapp.domain.usecase.IsMovieInFavoritesUseCase
import com.example.movieapp.domain.usecase.RemoveFromFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val getMovieRecommendationUseCase: GetMovieRecommendationUseCase,
    private val insertFavoriteMovieUseCase: InsertFavoriteMovieUseCase,
    private val isMovieInFavoritesUseCase: IsMovieInFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
    private val preferences: Preferences
) : ViewModel() {

    private val _movieDetailUI = MutableStateFlow<MovieDetailUI?>(null)
    val movieDetailUI = _movieDetailUI.asStateFlow()

    private val _moviesListUIState = MutableStateFlow<MovieListUI?>(null)
    val moviesListUIState: StateFlow<MovieListUI?> = _moviesListUIState.asStateFlow()

    private val _isMovieInFavoriteList = MutableStateFlow<Boolean>(false)
    val isMovieInFavoriteList: StateFlow<Boolean> = _isMovieInFavoriteList.asStateFlow()

    fun getMovieDetail(movieID: Int, releaseDate: String) {
        viewModelScope.launch {
            getMovieDetailUseCase(GetMovieDetailUseCase.Params(movieID))
                .collect {
                    _movieDetailUI.value = it.data
                    _movieDetailUI.value = _movieDetailUI.value?.copy(
                        releaseDate = releaseDate
                    )
                }
        }
    }

    fun getRecommendedMovies(movieID: Int) {
        viewModelScope.launch {
            getMovieRecommendationUseCase(GetMovieRecommendationUseCase.Params(movieID))
                .collect {
                    _moviesListUIState.value = it.data
                }
        }
    }

    fun addMovieToFavoriteList(movieID: Int) {
        viewModelScope.launch {
            _movieDetailUI.value?.let {
                insertFavoriteMovieUseCase(
                    InsertFavoriteMovieUseCase.Params(
                        userName = preferences.getUserPrefs().username,
                        it,
                        movieID
                    )
                )
            }
        }
    }

    fun isMovieInFavoriteList(movieID: Int) {
        viewModelScope.launch {
            isMovieInFavoritesUseCase(
                IsMovieInFavoritesUseCase.Params(
                    userName = preferences.getUserPrefs().username,
                    movieID = movieID
                )
            ).collect {
                _isMovieInFavoriteList.value = it
            }
        }
    }

    fun removeMovieFromFavoriteList(movieID: Int) {
        viewModelScope.launch {
            _movieDetailUI.value?.let {
                removeFromFavoritesUseCase(
                    RemoveFromFavoritesUseCase.Params(
                        userName = preferences.getUserPrefs().username,
                        it,
                        movieID
                    )
                )
            }
        }
    }
}
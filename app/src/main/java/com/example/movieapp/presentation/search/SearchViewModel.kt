package com.example.movieapp.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.domain.model.MovieListUI
import com.example.movieapp.domain.usecase.SearchMoviesUseCase
import com.example.movieapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase
) : ViewModel(){

    private var _searchMovieListUIState = MutableStateFlow<MovieListUI?>(null)
    val searchMovieListUIState: StateFlow<MovieListUI?> =  _searchMovieListUIState.asStateFlow()


    private var job: Job? = null

    fun searchMovie (keyword : String) {
        job?.cancel()
        job = viewModelScope.launch {
            searchMoviesUseCase(SearchMoviesUseCase.Params(
                keyword
            )).collect{movieListUI->
                when (movieListUI) {
                    is Resource.Loading-> {
                    }

                    is Resource.Success -> {
                      _searchMovieListUIState.value = movieListUI.data
                    }

                    else -> { Unit }
                }

            }
        }
    }

    fun clearSearchResults() {
        _searchMovieListUIState.value = MovieListUI (
            movieList = emptyList()
        )
    }
}
package com.example.movieapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.domain.model.MovieInfo
import com.example.movieapp.domain.model.MovieListUI
import com.example.movieapp.domain.usecase.GetMoviesUseCase
import com.example.movieapp.domain.usecase.MovieDisplayType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase
) : ViewModel() {

    private val _moviesListUIState = MutableStateFlow<MovieListUI?>(null)
    val moviesListUIState: StateFlow<MovieListUI?> = _moviesListUIState.asStateFlow()


     fun fetchMovies(){
       viewModelScope.launch {
           val trendingFlow = getMoviesUseCase(GetMoviesUseCase.Params(MovieDisplayType.TRENDING))
           val popularFlow = getMoviesUseCase(GetMoviesUseCase.Params(MovieDisplayType.POPULAR))
           val topRatedFlow = getMoviesUseCase(GetMoviesUseCase.Params(MovieDisplayType.TOP_RATED))
           val upcomingFlow = getMoviesUseCase(GetMoviesUseCase.Params(MovieDisplayType.UPCOMING))

           merge(trendingFlow,popularFlow,topRatedFlow,upcomingFlow).collect{
               val allMovies = mutableListOf<MovieInfo>()
               allMovies.addAll(it.data?.movieList ?: listOf())
               _moviesListUIState.value = MovieListUI(allMovies)
           }

       }
    }

}

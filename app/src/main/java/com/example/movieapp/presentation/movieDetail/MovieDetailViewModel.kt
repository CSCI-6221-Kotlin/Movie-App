package com.example.movieapp.presentation.movieDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.domain.model.MovieDetailUI
import com.example.movieapp.domain.usecase.GetMovieDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val getMovieDetailUseCase: GetMovieDetailUseCase
) : ViewModel(){

    private val _movieDetailUI = MutableStateFlow<MovieDetailUI?>(null)
    val movieDetailUI = _movieDetailUI.asStateFlow()

    fun getMovieDetail(movieID: Int) {
        viewModelScope.launch {
            getMovieDetailUseCase(GetMovieDetailUseCase.Params(movieID))
                .collect {
                    _movieDetailUI.value = it.data
                }
        }
    }
}
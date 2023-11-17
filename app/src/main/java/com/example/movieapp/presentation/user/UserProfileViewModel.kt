package com.example.movieapp.presentation.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.domain.model.MovieInfo
import com.example.movieapp.domain.preferences.Preferences
import com.example.movieapp.domain.usecase.GetFavoriteMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val getFavoriteMovieUseCase: GetFavoriteMovieUseCase,
    private val preferences: Preferences
) : ViewModel() {

    private var _favoriteMovies = MutableStateFlow<List<MovieInfo>?>(null)
    var favoriteMovies : StateFlow<List<MovieInfo>?> = _favoriteMovies.asStateFlow()

    fun getFavoriteMovies(){
        viewModelScope.launch {
            getFavoriteMovieUseCase(GetFavoriteMovieUseCase.Params(
                userName = preferences.getUserPrefs().username
            )).collect{ favoriteMovies->
               _favoriteMovies.value = favoriteMovies
            }
        }
    }
}
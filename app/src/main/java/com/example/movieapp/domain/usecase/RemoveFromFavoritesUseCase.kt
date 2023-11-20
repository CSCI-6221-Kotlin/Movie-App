package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.model.MovieDetailUI
import com.example.movieapp.domain.repository.MoviesRepository
import javax.inject.Inject

class RemoveFromFavoritesUseCase @Inject constructor(
    private val moviesRepository: MoviesRepository
) {

    data class Params(
        val userName: String,
        val movieDetailUI: MovieDetailUI,
        val movieID: Int
    )

    suspend operator fun invoke(params:Params) {
        moviesRepository.removeFromFavorites(params.userName,params.movieDetailUI,params.movieID)
    }

}
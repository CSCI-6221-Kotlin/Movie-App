package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsMovieInFavoritesUseCase @Inject constructor(
    private val moviesRepository: MoviesRepository
) {

    data class Params(
        val userName: String,
        val movieID: Int,
    )

     suspend operator fun invoke(params: Params) : Flow<Boolean> {
        return moviesRepository.isMovieInFavoriteList(params.userName, params.movieID)
    }
}
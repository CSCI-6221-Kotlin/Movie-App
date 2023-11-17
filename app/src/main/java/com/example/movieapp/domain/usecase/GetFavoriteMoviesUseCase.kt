package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.model.MovieInfo
import com.example.movieapp.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteMovieUseCase @Inject constructor(
    private val moviesRepository: MoviesRepository
) {

    data class Params(val userName : String)

     operator fun invoke(params:Params) : Flow<List<MovieInfo>> {
        return moviesRepository.getFavoriteMovies(params.userName)
    }
}
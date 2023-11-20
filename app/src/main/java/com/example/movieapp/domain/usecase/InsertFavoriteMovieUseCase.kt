package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.model.MovieDetailUI
import com.example.movieapp.domain.repository.MoviesRepository
import javax.inject.Inject

class InsertFavoriteMovieUseCase @Inject constructor(
    private val moviesRepository: MoviesRepository
) {

    data class Params(val userName : String, val movieDetailUI: MovieDetailUI, val movieID: Int)

    suspend operator fun invoke(params:Params){
        moviesRepository.insertFavoriteMovie(params.userName,params.movieDetailUI,params.movieID)
    }
}
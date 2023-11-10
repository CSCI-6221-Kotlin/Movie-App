package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.model.MovieDetailUI
import com.example.movieapp.domain.repository.MoviesRepository
import com.example.movieapp.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val repository: MoviesRepository
) {
    data class Params (
        val id : Int
    )

    suspend operator fun invoke(params:Params) : Flow<Resource<MovieDetailUI>>{
        return repository.getMovieDetail(params.id)
    }
}
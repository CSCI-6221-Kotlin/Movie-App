package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.model.MovieListUI
import com.example.movieapp.domain.repository.MoviesRepository
import com.example.movieapp.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetMoviesUseCase @Inject constructor(
    private val repository: MoviesRepository
) {

    data class Params (
        val movieDisplayType : MovieDisplayType
    )

     suspend operator fun invoke(params: Params) : Flow<Resource<MovieListUI>> {
         return when(params.movieDisplayType){
             MovieDisplayType.TRENDING -> {
                 repository.getTrendingMovies(MovieDisplayType.TRENDING)
             }

             MovieDisplayType.UPCOMING -> {
                 repository.getUpcomingMovies(params.movieDisplayType)
             }

             MovieDisplayType.POPULAR -> {
                 repository.getPopularMovies(params.movieDisplayType)
             }

             MovieDisplayType.TOP_RATED -> {
                 repository.getTopRatedMovies(params.movieDisplayType)
             }
         }
    }

}

enum class MovieDisplayType{
    TRENDING, POPULAR, TOP_RATED,UPCOMING
}


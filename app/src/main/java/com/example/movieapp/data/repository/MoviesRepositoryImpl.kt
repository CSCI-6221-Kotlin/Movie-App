package com.example.movieapp.data.repository

import com.example.movieapp.data.mapper.toMovieDetailUI
import com.example.movieapp.data.mapper.toMovieListUI
import com.example.movieapp.data.remote.TMDBApi
import com.example.movieapp.data.remote.dto.MovieDetailResponse
import com.example.movieapp.domain.model.MovieDetailUI
import com.example.movieapp.domain.model.MovieListUI
import com.example.movieapp.domain.repository.MoviesRepository
import com.example.movieapp.domain.usecase.MovieDisplayType
import com.example.movieapp.util.Constants
import com.example.movieapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(
    private val api: TMDBApi
) : MoviesRepository {

    override suspend fun getTopRatedMovies(movieDisplayType: MovieDisplayType): Flow<Resource<MovieListUI>> =
        flow {
            try {
                val response = api.getTopRatedMovies()
                if (response.isSuccessful) {
                    response.body()?.let { movieListDTO ->
                        emit(
                            Resource.Success(
                                movieListDTO.toMovieListUI(movieDisplayType)
                            )
                        )
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                emit(Resource.Error(exception.toString()))
            }
        }

    override suspend fun getPopularMovies(movieDisplayType: MovieDisplayType): Flow<Resource<MovieListUI>> =
        flow {
            try {
                val response = api.getPopularMovies()
                if (response.isSuccessful) {
                    response.body()?.let { movieListDTO ->
                        emit(
                            Resource.Success(
                                movieListDTO.toMovieListUI(movieDisplayType)
                            )
                        )
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                emit(Resource.Error(exception.toString()))
            }
        }

    override suspend fun getUpcomingMovies(movieDisplayType: MovieDisplayType): Flow<Resource<MovieListUI>> =
        flow {
            try {
                val response = api.getUpComingMovies()
                if (response.isSuccessful) {
                    response.body()?.let { movieListDTO ->
                        emit(
                            Resource.Success(
                                movieListDTO.toMovieListUI(movieDisplayType)
                            )
                        )
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                emit(Resource.Error(exception.toString()))
            }
        }

    override suspend fun getTrendingMovies(movieDisplayType: MovieDisplayType): Flow<Resource<MovieListUI>> =
        flow {
            try {
                val response = api.getTrendingMovies(timeWindow = Constants.TIME_WINDOW)
                if (response.isSuccessful) {
                    response.body()?.let { movieListDTO ->
                        emit(
                            Resource.Success(
                                movieListDTO.toMovieListUI(movieDisplayType)
                            )
                        )
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                emit(Resource.Error(exception.toString()))
            }
        }

    override suspend fun getMovieDetail(movieID: Int): Flow<Resource<MovieDetailUI>> = flow {
        try {
            val response = api.getMovieDetail(movieID = movieID)
            if (response.isSuccessful) {
                response.body()?.let { movieDetailResponse ->
                    emit(
                        Resource.Success(
                            movieDetailResponse.toMovieDetailUI()
                        )
                    )
                }
            }
        } catch (exception: Exception) {
           exception.printStackTrace()
           emit(Resource.Error(exception.toString()))
        }
    }
}
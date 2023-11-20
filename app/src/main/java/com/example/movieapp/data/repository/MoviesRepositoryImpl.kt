package com.example.movieapp.data.repository

import com.example.movieapp.data.local.FavoriteMoviesDAO
import com.example.movieapp.data.mapper.toMovieDetailEntity
import com.example.movieapp.data.mapper.toMovieDetailUI
import com.example.movieapp.data.mapper.toMovieInfo
import com.example.movieapp.data.mapper.toMovieListUI
import com.example.movieapp.data.remote.TMDBApi
import com.example.movieapp.domain.model.MovieDetailUI
import com.example.movieapp.domain.model.MovieInfo
import com.example.movieapp.domain.model.MovieListUI
import com.example.movieapp.domain.repository.MoviesRepository
import com.example.movieapp.domain.usecase.MovieDisplayType
import com.example.movieapp.util.Constants
import com.example.movieapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(
    private val api: TMDBApi,
    private val dao: FavoriteMoviesDAO
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

    override suspend fun getRecommendedMovies(movieID: Int): Flow<Resource<MovieListUI>> =
        flow {
            try {
                val response = api.getRecommendation(movieID)
                if (response.isSuccessful) {
                    response.body()?.let { movieListDTO ->
                        emit(
                            Resource.Success(
                                movieListDTO.toMovieListUI(null)
                            )
                        )
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                emit(Resource.Error(exception.toString()))
            }
        }

    override suspend fun insertFavoriteMovie(userName:String, movieDetailUI: MovieDetailUI, movieID: Int) {
        dao.insertFavoriteMovie(movieDetailUI.toMovieDetailEntity(userName,movieID))
    }

    override fun getFavoriteMovies(userName: String): Flow<List<MovieInfo>>  {
        return dao.getFavoriteMovies(
            userName = userName
        ).map { movies->
            movies.map {
                it.toMovieInfo()
            }
        }
    }

    override suspend fun isMovieInFavoriteList(userName: String, movieID: Int): Flow<Boolean> {
        val count = dao.isMovieInFavoriteList(userName,movieID)
        return flowOf(count>0)
    }

    override suspend fun removeFromFavorites(
        userName: String,
        movieDetailUI: MovieDetailUI,
        movieID: Int
    ) {
        dao.removeFromFavorites(movieDetailUI.toMovieDetailEntity(userName,movieID))
    }

}
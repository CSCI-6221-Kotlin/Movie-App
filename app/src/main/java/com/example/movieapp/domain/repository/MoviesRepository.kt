package com.example.movieapp.domain.repository

import com.example.movieapp.domain.model.MovieDetailUI
import com.example.movieapp.domain.model.MovieInfo
import com.example.movieapp.domain.model.MovieListUI
import com.example.movieapp.domain.usecase.MovieDisplayType
import com.example.movieapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface MoviesRepository {
    suspend fun getTopRatedMovies(movieDisplayType: MovieDisplayType) : Flow<Resource<MovieListUI>>
    suspend fun getPopularMovies(movieDisplayType: MovieDisplayType) : Flow<Resource<MovieListUI>>
    suspend fun getUpcomingMovies(movieDisplayType: MovieDisplayType) : Flow<Resource<MovieListUI>>
    suspend fun getTrendingMovies(movieDisplayType: MovieDisplayType) : Flow<Resource<MovieListUI>>
    suspend fun getMovieDetail(movieID: Int): Flow<Resource<MovieDetailUI>>
    suspend fun getRecommendedMovies(movieID: Int) : Flow<Resource<MovieListUI>>
    suspend fun insertFavoriteMovie(userName:String, movieDetailUI: MovieDetailUI , movieID: Int)
    fun getFavoriteMovies(userName : String) : Flow<List<MovieInfo>>
    suspend fun isMovieInFavoriteList(userName:String, movieID: Int) : Flow<Boolean>
    suspend fun removeFromFavorites(userName: String, movieDetailUI: MovieDetailUI, movieID: Int)
}
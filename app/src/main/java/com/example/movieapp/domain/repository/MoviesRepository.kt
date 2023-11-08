package com.example.movieapp.domain.repository

import com.example.movieapp.domain.model.MovieListUI
import com.example.movieapp.domain.usecase.MovieDisplayType
import com.example.movieapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface MoviesRepository {
    suspend fun getTopRatedMovies(movieDisplayType: MovieDisplayType) : Flow<Resource<MovieListUI>>
    suspend fun getPopularMovies(movieDisplayType: MovieDisplayType) : Flow<Resource<MovieListUI>>
    suspend fun getUpcomingMovies(movieDisplayType: MovieDisplayType) : Flow<Resource<MovieListUI>>
    suspend fun getTrendingMovies(movieDisplayType: MovieDisplayType) : Flow<Resource<MovieListUI>>
}
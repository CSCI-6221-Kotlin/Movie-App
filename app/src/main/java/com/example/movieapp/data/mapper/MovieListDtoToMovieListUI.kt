package com.example.movieapp.data.mapper

import com.example.movieapp.core.toSafeList
import com.example.movieapp.data.remote.dto.MovieListDTO
import com.example.movieapp.domain.model.MovieInfo
import com.example.movieapp.domain.model.MovieListUI
import com.example.movieapp.domain.usecase.MovieDisplayType

fun MovieListDTO.toMovieListUI(movieDisplayType: MovieDisplayType):MovieListUI{
    val resultList = results?.map {
        MovieInfo(
            id = it.id,
            originalTitle = it.originalTitle,
            posterPath = it.posterPath,
            movieDisplayType = movieDisplayType
        )
    }
    return MovieListUI(
        movieList = resultList.toSafeList()
    )
}
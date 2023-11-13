package com.example.movieapp.data.mapper

import com.example.movieapp.core.toSafeList
import com.example.movieapp.data.remote.dto.MovieListDTO
import com.example.movieapp.domain.model.MovieInfo
import com.example.movieapp.domain.model.MovieListUI
import com.example.movieapp.domain.usecase.MovieDisplayType

fun MovieListDTO.toMovieListUI(movieDisplayType: MovieDisplayType?):MovieListUI{
    val resultList = results?.map {
        MovieInfo(
            id = it.id,
            originalTitle = it.originalTitle,
            posterPath = it.posterPath,
            movieDisplayType = movieDisplayType
        )
    }
    return MovieListUI(
        // Having issues with aspect ratio when rendering error image when poster path is not found. Current workaround solution: remove fro list.
        movieList = resultList.toSafeList().filter{ it.posterPath != null }
    )
}
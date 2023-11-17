package com.example.movieapp.data.mapper

import com.example.movieapp.data.local.entity.MovieDetailEntity
import com.example.movieapp.domain.model.MovieDetailUI
import com.example.movieapp.domain.model.MovieInfo

fun MovieDetailEntity.toMovieInfo(): MovieInfo {
    return MovieInfo(
        id = movieID ?: 0,
        originalTitle = title,
        posterPath = posterPath,
    )
}

fun MovieDetailUI.toMovieDetailEntity(userName:String,movieID:Int): MovieDetailEntity {
    return MovieDetailEntity(
        movieID = movieID,
        userName = userName,
        movieTitle = title,
        posterPath = posterPath,
        overview = overview,
        homepage = homepage,
        title = title,
        releaseDate = releaseDate,
        voteAverage = voteAverage)
}


package com.example.movieapp.data.mapper

import com.example.movieapp.core.toDateFormat
import com.example.movieapp.core.toSafeString
import com.example.movieapp.data.remote.dto.MovieDetailResponse
import com.example.movieapp.domain.model.MovieDetailUI

fun MovieDetailResponse.toMovieDetailUI():MovieDetailUI{
    return MovieDetailUI(
        posterPath = posterPath.toSafeString(),
        voteAverage = voteAverage,
        releaseDate = releaseDate.toSafeString().toDateFormat(),
        overview = overview.toSafeString(),
        homepage = homepage.toSafeString(),
        title = originalTitle.toSafeString()
    )
}
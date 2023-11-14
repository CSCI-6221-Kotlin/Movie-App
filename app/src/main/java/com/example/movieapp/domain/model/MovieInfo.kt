package com.example.movieapp.domain.model

import com.example.movieapp.domain.usecase.MovieDisplayType

data class MovieInfo(
    val id:Int,
    val originalTitle:String,
    val posterPath:String?,
    val movieDisplayType: MovieDisplayType?
)

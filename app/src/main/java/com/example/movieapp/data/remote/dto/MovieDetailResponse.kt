package com.example.movieapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieDetailResponse(
    @SerializedName("original_title")
    val originalTitle: String,
    @SerializedName("poster_path")
    val posterPath: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("release_date")
    val releaseDate: String,
    val overview: String,
    val homepage:String,
)

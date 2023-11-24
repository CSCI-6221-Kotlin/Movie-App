package com.example.movieapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieListDTO(
    val page: Int?,
    val results: List<Result>?,
    @SerializedName("total_pages")
    val totalPages: Int?,
    @SerializedName("total_results")
    val totalResults: Int?
)
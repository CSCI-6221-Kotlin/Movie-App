package com.example.movieapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "favorite_movies")
data class MovieDetailEntity(
 @PrimaryKey val movieID: Int? = null,
 val userName : String,
 val movieTitle: String,
 val posterPath : String,
 val voteAverage: Double,
 val releaseDate: String,
 val overview:String,
 val homepage:String,
 val title:String
)


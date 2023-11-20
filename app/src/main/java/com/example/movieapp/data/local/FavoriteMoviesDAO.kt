package com.example.movieapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movieapp.data.local.entity.MovieDetailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteMoviesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteMovie(movieDetailEntity: MovieDetailEntity)

    @Query ("SELECT * FROM favorite_movies WHERE userName = :userName")
    fun getFavoriteMovies(userName : String) : Flow<List<MovieDetailEntity>>

    @Query("SELECT COUNT(*) FROM favorite_movies WHERE userName = :userName AND movieId = :movieId")
    suspend fun isMovieInFavoriteList(userName: String, movieId: Int): Int

    @Delete
    suspend fun removeFromFavorites(movieDetailEntity: MovieDetailEntity)
}
package com.example.movieapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.movieapp.data.local.entity.MovieDetailEntity

@Database(
    entities = [MovieDetailEntity::class],
    version = 1
)
abstract class MoviesDatabase : RoomDatabase() {
    abstract val dao : FavoriteMoviesDAO

    companion object {
        const val DATABASE_NAME = "movies_db"
    }
}
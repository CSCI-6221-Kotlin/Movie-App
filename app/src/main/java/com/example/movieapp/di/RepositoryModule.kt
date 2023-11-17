package com.example.movieapp.di

import com.example.movieapp.data.local.MoviesDatabase
import com.example.movieapp.data.remote.TMDBApi
import com.example.movieapp.data.repository.MoviesRepositoryImpl
import com.example.movieapp.domain.repository.MoviesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMoviesRepository(
        api: TMDBApi,
        db: MoviesDatabase
    ): MoviesRepository {
        return MoviesRepositoryImpl(
            api = api,
            dao = db.dao
        )
    }
}
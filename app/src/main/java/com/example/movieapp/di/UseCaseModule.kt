package com.example.movieapp.di

import com.example.movieapp.domain.repository.MoviesRepository
import com.example.movieapp.domain.usecase.GetMoviesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Singleton
    @Provides
    fun provideGetMoviesUseCase(repository: MoviesRepository) : GetMoviesUseCase {
        return GetMoviesUseCase(
            repository
        )
    }
}
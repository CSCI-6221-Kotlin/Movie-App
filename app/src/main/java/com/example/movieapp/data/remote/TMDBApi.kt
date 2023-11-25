package com.example.movieapp.data.remote

import com.example.movieapp.BuildConfig
import com.example.movieapp.data.remote.dto.MovieDetailResponse
import com.example.movieapp.data.remote.dto.MovieListDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBApi {

    @GET("trending/movie/{time_window}")
    suspend fun getTrendingMovies(
        @Path("time_window") timeWindow: String,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
    ): Response<MovieListDTO>

    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("api_key") apiKey: String = BuildConfig.API_KEY): Response<MovieListDTO>

    @GET("movie/upcoming")
    suspend fun getUpComingMovies(
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("region") region: String = "US"
    ): Response<MovieListDTO>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(@Query("api_key") apiKey: String = BuildConfig.API_KEY): Response<MovieListDTO>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieID: Int,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("region") region: String = "US"
    ): Response<MovieDetailResponse>

    @GET("movie/{movie_id}/recommendations")
    suspend fun getRecommendation(
        @Path("movie_id") movieID: Int,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY
    ): Response<MovieListDTO>

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("region") region: String = "US",
        @Query("query") query:String,
    ) : Response<MovieListDTO>

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
    }

}
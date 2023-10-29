package com.example.movieapp.core

fun <T> List<T>?.toSafeList(): List<T> {
    return this ?: emptyList()
}
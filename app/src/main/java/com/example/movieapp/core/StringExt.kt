package com.example.movieapp.core

fun String?.toSafeString(): String {
    return this ?: ""
}
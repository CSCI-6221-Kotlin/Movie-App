package com.example.movieapp.domain.preferences

import com.example.movieapp.firebase.UserDatabase

interface Preferences {
    fun saveUserName(userName : String)
    fun saveEmail(email : String)
    fun getUserPrefs (): UserDatabase

    companion object {
        const val KEY_USER_NAME = "userName"
        const val KEY_USER_EMAIL = "userEmail"
    }
}
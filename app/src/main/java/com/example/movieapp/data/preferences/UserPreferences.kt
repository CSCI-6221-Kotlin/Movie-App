package com.example.movieapp.data.preferences

import android.content.SharedPreferences
import com.example.movieapp.core.toSafeString
import com.example.movieapp.domain.preferences.Preferences
import com.example.movieapp.firebase.UserDatabase
import javax.inject.Inject

class UserPreferences @Inject constructor(
    private val sharedPref: SharedPreferences
) : Preferences {

    override fun saveUserName(userName: String) {
        sharedPref.edit().putString(Preferences.KEY_USER_NAME,userName).apply()
    }

    override fun saveEmail(email: String) {
        sharedPref.edit().putString(Preferences.KEY_USER_EMAIL,email).apply()
    }

    override fun getUserPrefs(): UserDatabase {
        val userName = sharedPref.getString(Preferences.KEY_USER_NAME,"")
        val userEmail = sharedPref.getString(Preferences.KEY_USER_EMAIL,"")
        return UserDatabase(userName.toSafeString(),userEmail.toSafeString())
    }
}
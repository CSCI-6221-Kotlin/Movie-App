package com.example.movieapp.core


import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun String?.toSafeString(): String {
    return this ?: ""
}

fun String?.toDateFormat(): String{
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val outputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    val date = LocalDate.parse(this,inputFormatter)
    return date.format(outputFormatter)
}

fun String?.convertToHttps(): String? {
    return this?.let { url ->
        if (url.startsWith("http://")) {
            "https://" + url.substring(7)
        } else if (url.startsWith("https://")) {
            url
        } else {
            url
        }
    }
}
package com.example.weathertogo.ui.utility

fun isValidLatitude(str: String): Boolean {
    return str.matches(Regex("^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?)$"))
}

fun isValidLongitude(str: String): Boolean {
    return str.matches(Regex("^[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$"))
}

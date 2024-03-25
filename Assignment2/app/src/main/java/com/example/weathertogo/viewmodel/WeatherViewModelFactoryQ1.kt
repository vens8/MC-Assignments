package com.example.weathertogo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class WeatherViewModelFactoryQ1() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = WeatherViewModelQ1() as T
}

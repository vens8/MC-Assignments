package com.example.weathertogo

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weathertogo.ui.LandingScreen
import com.example.weathertogo.ui.Q1Screen
import com.example.weathertogo.ui.Q2Screen
import com.example.weathertogo.ui.theme.WeatherToGoTheme
import com.example.weathertogo.viewmodel.WeatherViewModelQ1
import com.example.weathertogo.viewmodel.WeatherViewModelFactoryQ1
import com.example.weathertogo.viewmodel.WeatherViewModelFactoryQ2
import com.example.weathertogo.viewmodel.WeatherViewModelQ2

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // creating our navController
            val navController = rememberNavController()

            val weatherViewModelQ1: WeatherViewModelQ1 = viewModel(factory = WeatherViewModelFactoryQ1())
            val weatherViewModelQ2: WeatherViewModelQ2 = viewModel(factory = WeatherViewModelFactoryQ2(this))

            WeatherToGoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = "landingScreen") {
                        composable("landingScreen") {
                            LandingScreen(navController)
                        }
                        composable("Q1Screen") {
                            Q1Screen(navController, weatherViewModelQ1)
                        }
                        composable("Q2Screen") {
                            Q2Screen(navController, weatherViewModelQ2)
                        }
                    }
                }
            }
        }
    }
}
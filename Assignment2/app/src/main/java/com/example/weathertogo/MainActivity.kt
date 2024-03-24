package com.example.weathertogo

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weathertogo.ui.LandingScreen
import com.example.weathertogo.ui.Q1Screen
import com.example.weathertogo.ui.theme.WeatherToGoTheme
import com.example.weathertogo.viewmodel.WeatherViewModel
import com.example.weathertogo.viewmodel.WeatherViewModelFactory

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // creating our navController
            val navController = rememberNavController()

            val weatherViewModel: WeatherViewModel = viewModel(factory = WeatherViewModelFactory())

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
                            Q1Screen(navController, weatherViewModel)
                        }
                        composable("Q2Screen") {
//                            Q1Screen(navController, weatherViewModel)
                        }
                    }
                }
            }
        }
    }
}
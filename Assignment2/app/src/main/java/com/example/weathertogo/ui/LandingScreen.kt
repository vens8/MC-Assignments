package com.example.weathertogo.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun LandingScreen(navController: NavHostController) {
    Column {
        Button(onClick = {
        navController.navigate("Q1Screen")
        }) {
            Text("Q1: Weather History")
        }
        Button(onClick = {
        navController.navigate("Q2Screen")
        }) {
            Text("Q2: (Your Functionality)")
        }
    }
}
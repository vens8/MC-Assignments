package com.example.weathertogo.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.weathertogo.R

@Composable
fun LandingScreen(navController: NavHostController) {
    val backgroundImage = painterResource(R.drawable.background)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        OutlinedCard(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(0.95f).fillMaxHeight(0.9f),
            border = BorderStroke(0.dp, MaterialTheme.colorScheme.onBackground),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
            ) {
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(top = 32.dp, bottom = 64.dp),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onBackground),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF294a9e), Color(0xFF7B83BF), Color(0xFFBCBFE0))
                                    )
                                )
                            ) {
                                append("WeatherToGo?")
                            }
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                    )
                }
                OutlinedButton(
                    onClick = { navController.navigate("Q1Screen") },
                    modifier = Modifier
                        .size(200.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Q1", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(top = 16.dp))
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Network API", color = Color(0xFFBCBFE0), style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Spacer(modifier = Modifier.size(32.dp))
                OutlinedButton(
                    onClick = { navController.navigate("Q2Screen") },
                    modifier = Modifier.size(200.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Q2", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(top = 32.dp))
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Network API",
                            color = Color(0xFFBCBFE0),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Text("+",
                            color = Color(0xFFBCBFE0),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Text("Room Persistence",
                            color = Color(0xFFBCBFE0),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }

            }
        }
    }
}

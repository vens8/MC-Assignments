package com.example.axelero.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainContent(xAngle: Float, yAngle: Float, zAngle: Float) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Real-Time Orientation")
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Text("X: ")
            Text("$xAngle")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text("Y: ")
            Text("$yAngle")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text("Z: ")
            Text("$zAngle")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                // Navigate to HistoryActivity
            }
        ) {
            Text("View History")
        }
    }
}
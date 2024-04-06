package com.example.axelero.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.axelero.HistoryActivity
import com.example.axelero.repository.OrientationDataRepository

@Composable
fun MainContent(xAngle: Float, yAngle: Float, zAngle: Float) {
    val context = LocalContext.current
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
                // Create an Intent to start HistoryActivity
                val intent = Intent(context, HistoryActivity::class.java)
                // Start HistoryActivity
                context.startActivity(intent)
            }
        ) {
            Text("View History")
        }
    }
}
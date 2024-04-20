package com.example.axelero.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.component.shape.shader.color
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.lineSeries


@Composable
fun LineChart(
    title: String,
    data: List<Float>,
    modelProducer: CartesianChartModelProducer,
) {
    val lineColor = when (title) {
        "X Angle" -> Color(0xFFE53935) // Red
        "Y Angle" -> Color(0xFF8E24AA) // Purple
        "Z Angle" -> Color(0xFF3949AB) // Blue
        else -> Color(0xFF546E7A) // Default Grey
    }
    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                listOf(
                    rememberLineSpec(
                        DynamicShaders.color(Color(0xffa485e0))
                    )
                )
            ),
//            startAxis = rememberStartAxis(label = TextComponent.build { Text("Time") }),
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(guideline = null)
        ),
        modelProducer = modelProducer,
        modifier = Modifier.fillMaxWidth()
    ) {
        LaunchedEffect(data) {
            Log.d("LineChart", "Updating line chart with new data")
            if (data.isNotEmpty()) {
                modelProducer.tryRunTransaction {
                    lineSeries {
                        Log.d("LineChart", "Adding series with data: ${data.toString()}")
                        series(data)
                    }
                }
            }
        }
        Text(title, modifier = Modifier.padding(bottom = 8.dp))
    }
    // Title & Legend
    Column(modifier = Modifier.padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier
                .size(10.dp)
                .background(color = lineColor, shape = MaterialTheme.shapes.small))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "x-axis: Time", style = MaterialTheme.typography.bodySmall)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier
                .size(10.dp)
                .background(color = lineColor, shape = MaterialTheme.shapes.small))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "y-axis: Values", style = MaterialTheme.typography.bodySmall)
        }
    }
}
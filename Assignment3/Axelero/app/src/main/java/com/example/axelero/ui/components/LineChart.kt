package com.example.axelero.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                listOf(
                    rememberLineSpec(
                        DynamicShaders.color(Color(0xffa485e0))
                    )
                )
            ),
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(guideline = null)
        ),
        modelProducer = modelProducer,
        modifier = Modifier.fillMaxWidth()
    ) {
        LaunchedEffect(data) {
            modelProducer.tryRunTransaction {
                lineSeries {
                    series(data)
                }
            }
        }
        Text(title, modifier = Modifier.padding(bottom = 8.dp))
    }
}
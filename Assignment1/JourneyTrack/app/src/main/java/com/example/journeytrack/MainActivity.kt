package com.example.journeytrack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : AppCompatActivity() {
    private lateinit var btnConvert: Button
    private lateinit var btnNextStop: Button
    private lateinit var tvStops: TextView

    private var stops = listOf("Stop 1", "Stop 2", "Stop 3", "Stop 4", "Stop 5")
    private var distancesInKm = listOf(10, 20, 30, 40, 50)
    private var distancesInMiles = distancesInKm.map { it * 0.621371 }
    private var currentStop = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnConvert = findViewById(R.id.btn_convert)
        btnNextStop = findViewById(R.id.btn_next_stop)
        tvStops = findViewById(R.id.tv_stops)

        btnConvert.setOnClickListener {
            if (btnConvert.text == "Convert to Miles") {
                btnConvert.text = "Convert to Kilometers"
                displayStopsInMiles()
            } else {
                btnConvert.text = "Convert to Miles"
                displayStopsInKilometers()
            }
        }

        btnNextStop.setOnClickListener {
            if (currentStop < stops.size) {
                currentStop++
                displayStops()
            }
        }

        displayStops()

        val composeView = findViewById<ComposeView>(R.id.compose_view)
        composeView.setContent {
            Column {
                StopsList(stops)
            }
        }
    }

    private fun displayStops() {
        if (btnConvert.text == "Convert to Miles") {
            displayStopsInKilometers()
        } else {
            displayStopsInMiles()
        }
    }

    private fun displayStopsInKilometers() {
        var text = ""
        for (i in stops.indices) {
            text += "${stops[i]}: ${distancesInKm[i]} km\n"
        }
        tvStops.text = text
    }

    private fun displayStopsInMiles() {
        var text = ""
        for (i in stops.indices) {
            text += "${stops[i]}: ${distancesInMiles[i]} miles\n"
        }
        tvStops.text = text
    }

    @Composable
    fun StopsList(stops: List<String>) {
        LazyColumn {
            items(stops) { stop ->
                Text(stop)
            }
        }
    }
}

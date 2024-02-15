package com.example.journeytrack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.google.android.material.switchmaterial.SwitchMaterial

data class Stop(
    val name: String,
    val distanceInKm: Double
) {
    val distanceInMiles: Double
        get() = distanceInKm * 0.621371
}

class MainActivity : AppCompatActivity() {
    private lateinit var btnNextStop: Button
    private lateinit var tvStops: TextView
    private lateinit var switchUnits: SwitchMaterial
    private lateinit var switchLists: SwitchMaterial

    private val normalStops = listOf(
        Stop("KRITI NAGAR", 1.4),
        Stop("SATGURU RAM SINGH MARG", 2.5),
        Stop("INDERLOK", 4.3),
        Stop("ASHOK PARK MAIN", 2.5),
        Stop("PUNJABI BAGH", 5.3),
        Stop("PUNJABI BAGH WEST", 1.9),
        Stop("SHIVAJI PARK", 4.1),
        Stop("MADIPUR", 5.0),
        Stop("PASCHIM VIHAR EAST", 2.8),
        Stop("PASCHIM VIHAR WEST", 3.1),
    )

    private val lazyStops = listOf(
        Stop("KASHMERE GATE", 2.3),
        Stop("LAL QUILA", 1.2),
        Stop("JAMA MASJID", 3.5),
        Stop("DELHI GATE", 0.8),
        Stop("ITO", 6.7),
        Stop("MANDI HOUSE",  4.2),
        Stop("JANPATH", 5.6),
        Stop("CENTRAL SECRETARIAT", 2.9),
        Stop("KHAN MARKET", 1.7),
        Stop("JLN STADIUM", 3.3),
        Stop("JANGPURA", 2.2),
        Stop("LAJPAT NAGAR", 6.1),
        Stop("MOOLCHAND", 4.4),
        Stop("KAILASH COLONY", 5.8),
        Stop("NEHRU PLACE", 3.6),
        Stop("KALKAJI MANDIR", 2.5),
        Stop("GOVIND PURI", 1.9),
        Stop("HARKESH NAGAR OKHLA", 6.3),
        Stop("JASOLA-APOLLO", 4.7),
        Stop("SARITA VIHAR", 5.1),
        Stop("MOHAN ESTATE", 3.9),
        Stop("TUGHLAKABAD STATION", 2.8),
        Stop("BADARPUR BORDER", 1.6)
    )
    private var currentStop = 0
    private var useLazyStops = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnNextStop = findViewById(R.id.btn_next_stop)

        switchUnits = findViewById(R.id.switch_units)
        switchLists = findViewById(R.id.switch_lists)

        switchUnits.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switchUnits.text = "Miles"
            } else {
                switchUnits.text = "KM"
            }
            displayStops()
        }

        switchLists.setOnCheckedChangeListener { _, isChecked ->
            useLazyStops = isChecked
            switchLists.text = if (useLazyStops) "Lazy Stops" else "Normal Stops"
            displayStops()
        }

        btnNextStop.setOnClickListener {
            val stops = if (useLazyStops) lazyStops else normalStops
            if (currentStop < stops.size) {
                currentStop++
                displayStops()
            }
        }

        displayStops()

        val composeView = findViewById<ComposeView>(R.id.compose_view)
        composeView.setContent {
            Column {
                StopsList(if (useLazyStops) lazyStops else normalStops, switchUnits.isChecked)
            }
        }
    }

    private fun displayStops() {
        val composeView = findViewById<ComposeView>(R.id.compose_view)
        composeView.setContent {
            if (useLazyStops) {
                LazyStopsList(lazyStops, switchUnits.isChecked)
            } else {
                StopsList(normalStops, switchUnits.isChecked)
            }
        }
    }

    @Composable
    fun StopsList(stops: List<Stop>, isMiles: Boolean) {
        Column {
            for (stop in stops) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stop.name, style = MaterialTheme.typography.h6)
                        Text(
                            text = if (isMiles) "${stop.distanceInMiles} miles" else "${stop.distanceInKm} km",
                            style = MaterialTheme.typography.subtitle1
                        )
                    }
                }
                Divider(color = Color.LightGray, thickness = 0.5.dp)
            }
        }
    }

    @Composable
    fun LazyStopsList(stops: List<Stop>, isMiles: Boolean) {
        LazyColumn {
            items(stops) { stop ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stop.name, style = MaterialTheme.typography.h6)
                        Text(
                            text = if (isMiles) "${stop.distanceInMiles} miles" else "${stop.distanceInKm} km",
                            style = MaterialTheme.typography.subtitle1
                        )
                    }
                }
                Divider(color = Color.LightGray, thickness = 0.5.dp)
            }
        }
    }
}
package com.example.journeytrack

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.material.switchmaterial.SwitchMaterial
import android.widget.Toast

data class Stop(
    val name: String,
    val distanceInKm: Double
) {
    val distanceInMiles: Double
        get() = (distanceInKm * 0.621371).round(2)
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}

class MainActivity : AppCompatActivity() {
    private lateinit var btnNextStop: Button
    private lateinit var switchUnits: SwitchMaterial
    private lateinit var switchLists: SwitchMaterial
    private lateinit var tvTotalDistanceCovered: TextView
    private lateinit var tvTotalDistanceRemaining: TextView

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
        Stop("BADARPUR BORDER", 1.6),
        Stop("SARAI", 2.1),
        Stop("NHPC CHOWK", 3.5),
        Stop("MEWALA MAHARAJAPUR", 6.7),
        Stop("SECTOR-28", 4.2),
        Stop("BADKAL MOR", 5.6),
        Stop("OLD FARIDABAD", 2.9),
        Stop("NEELAM CHOWK AJRONDA", 1.7),
        Stop("BATA CHOWK", 3.3),
        Stop("ESCORTS MUJESAR", 2.2),
        Stop("SANT SURDAS", 6.1),
        Stop("RAJA NAHAR SINGH", 4.4)
    )
    private var currentStop by mutableIntStateOf(0)
    private var useLazyStops by mutableStateOf(false)
    private var progressState by mutableFloatStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnNextStop = findViewById(R.id.btn_next_stop)
        switchUnits = findViewById(R.id.switch_units)
        switchLists = findViewById(R.id.switch_lists)
        tvTotalDistanceCovered = findViewById(R.id.tv_total_distance_covered)
        tvTotalDistanceRemaining = findViewById(R.id.tv_total_distance_remaining)

        switchUnits.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switchUnits.text = "Miles"
            } else {
                switchUnits.text = "KM"
            }
            displayStops()
        }

        switchLists.setOnCheckedChangeListener { _, isChecked ->
            currentStop = 0
            useLazyStops = isChecked
            updateProgressBar(if (useLazyStops) lazyStops.size else normalStops.size)
            switchLists.text = if (useLazyStops) "Lazy Stops" else "Normal Stops"
            displayStops()
        }

        btnNextStop.setOnClickListener {
            val stops = if (useLazyStops) lazyStops else normalStops
            if (currentStop < stops.size - 1) {
                currentStop++
                updateProgressBar(stops.size)
                displayStops()
                if (currentStop == stops.size - 1) {
                    showToast("Yay! You have reached the final stop :)")
                }
            } else {
                // Display a message when the user tries to click "Next Stop" after reaching the final stop
                showToast("You are already at the final stop! Reset by toggling stop lists.")
            }
        }

        displayStops()
        updateProgressBar(if (useLazyStops) lazyStops.size else normalStops.size)

        val logoView = findViewById<ComposeView>(R.id.logo_view)
        logoView.setContent {
            AppLogo()
        }
        val progressView = findViewById<ComposeView>(R.id.progress_view)
        progressView.setContent {
            ProgressBar()
        }

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
        var totalDistanceCovered = calculateTotalDistanceCovered(stops, currentStop, isMiles)
        var totalDistanceRemaining = calculateTotalDistanceRemaining(stops, currentStop, isMiles)
        val unit = if (isMiles) "miles" else "km"
        tvTotalDistanceCovered.text = "Total Distance Covered: ${totalDistanceCovered.round(2)} $unit"
        tvTotalDistanceRemaining.text = "Total Distance Remaining: ${totalDistanceRemaining.round(2)} $unit"

        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Column {
                for (i in stops.indices) {
                    val stop = stops[i]

                    val isCurrentStop = i == currentStop
                    if (isCurrentStop) {
                        totalDistanceCovered = calculateTotalDistanceCovered(stops, currentStop, isMiles)
                        totalDistanceRemaining = calculateTotalDistanceRemaining(stops, currentStop, isMiles)
                    }
                    StopCard(stop, isMiles, isCurrentStop)
                    Divider(color = Color.LightGray, thickness = 0.5.dp)
                    // Update text views
                    tvTotalDistanceCovered.text = "Total Distance Covered: ${totalDistanceCovered.round(2)} $unit"
                    tvTotalDistanceRemaining.text = "Total Distance Remaining: ${totalDistanceRemaining.round(2)} $unit"
                }
            }
        }
        // Automatically scroll to the current stop
        LaunchedEffect(key1 = currentStop) {
            // Calculate the scroll position based on the index and the height of each item
            val scrollPosition = currentStop * 250  // value obtained from trial and error
            scrollState.scrollTo(scrollPosition)
        }
    }

    @Composable
    fun LazyStopsList(stops: List<Stop>, isMiles: Boolean) {
        var totalDistanceCovered = calculateTotalDistanceCovered(stops, currentStop, isMiles)
        var totalDistanceRemaining = calculateTotalDistanceRemaining(stops, currentStop, isMiles)
        val unit = if (isMiles) "miles" else "km"
        tvTotalDistanceCovered.text = "Total Distance Covered: ${totalDistanceCovered.round(2)} $unit"
        tvTotalDistanceRemaining.text = "Total Distance Remaining: ${totalDistanceRemaining.round(2)} $unit"

        val listState = rememberLazyListState()

        LazyColumn(state = listState) {
            itemsIndexed(stops) { index, stop ->

                val isCurrentStop = index == currentStop
                if (isCurrentStop) {
                    totalDistanceCovered = calculateTotalDistanceCovered(stops, currentStop, isMiles)
                    totalDistanceRemaining = calculateTotalDistanceRemaining(stops, currentStop, isMiles)
                }
                StopCard(stop, isMiles, isCurrentStop)
                Divider(color = Color.LightGray, thickness = 0.5.dp)
                // Update text views
                tvTotalDistanceCovered.text = "Total Distance Covered: ${totalDistanceCovered.round(2)} $unit"
                tvTotalDistanceRemaining.text = "Total Distance Remaining: ${totalDistanceRemaining.round(2)} $unit"
            }
        }
        // Automatically scroll to the current stop
        LaunchedEffect(key1 = currentStop) {
            listState.animateScrollToItem(currentStop)
        }
    }


    @Composable
    fun StopCard(stop: Stop, isMiles: Boolean, isCurrentStop: Boolean) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        stop.name,
                        style = MaterialTheme.typography.h6,
                        color = if (isCurrentStop) MaterialTheme.colors.primary else Color.Unspecified
                    )
                    if (isCurrentStop) {
                        Text(
                            "Current Stop",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.primary
                        )
                    }
                }

                Text(
                    text = if (isMiles) "${stop.distanceInMiles} miles" else "${stop.distanceInKm} km",
                    style = MaterialTheme.typography.subtitle1
                )
            }
        }
    }


    private fun calculateTotalDistanceCovered(stops: List<Stop>, currentStop: Int, isMiles: Boolean): Double {
        var totalDistance = 0.0
        for (i in 0..currentStop) {
            totalDistance += if (isMiles) stops[i].distanceInMiles else stops[i].distanceInKm
        }
        return totalDistance
    }

    private fun calculateTotalDistanceRemaining(stops: List<Stop>, currentStop: Int, isMiles: Boolean): Double {
        var totalDistance = 0.0
        for (i in currentStop + 1 until stops.size) {
            totalDistance += if (isMiles) stops[i].distanceInMiles else stops[i].distanceInKm
        }
        return totalDistance
    }

    @Composable
    fun ProgressBar() {
        val color = MaterialTheme.colors.primary
        val backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f)

        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(MaterialTheme.shapes.small)
                .background(backgroundColor),
            progress = this.progressState,
            color = color
        )
    }

    private fun updateProgressBar(totalStops: Int) {
        progressState = ((currentStop.toFloat() + 1) / totalStops.toFloat())
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    @Composable
    fun AppLogo() {
        val image = painterResource(id = R.drawable.journeytracklogo)
        Image(painter = image, contentDescription = "My Image", modifier = Modifier.size(400.dp))
    }
}
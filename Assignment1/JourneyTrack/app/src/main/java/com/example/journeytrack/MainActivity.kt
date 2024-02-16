package com.example.journeytrack

import android.os.Bundle
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
        Stop("BADARPUR BORDER", 1.6)
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
            progressState = 0f
            useLazyStops = isChecked
            switchLists.text = if (useLazyStops) "Lazy Stops" else "Normal Stops"
            displayStops()
        }

        btnNextStop.setOnClickListener {
            val stops = if (useLazyStops) lazyStops else normalStops
            if (currentStop < stops.size) {
                currentStop++
                updateProgressBar(stops.size)
                displayStops()
            }
        }

        displayStops()

        val logoView = findViewById<ComposeView>(R.id.logo_view)
        logoView.setContent {
            AppLogo()
        }

        val progressView = findViewById<ComposeView>(R.id.progress_view)
        progressView.setContent {
            DisplayContent(stops = if (useLazyStops) lazyStops else normalStops, isMiles = switchUnits.isChecked,  currentStop = currentStop, progressState = progressState)
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
    fun DisplayContent(
        stops: List<Stop>,
        isMiles: Boolean,
        currentStop: Int,
        progressState: Float
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            ProgressBar()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                for (i in stops.indices) {
                    val stop = stops[i]
                    StopCard(stop, isMiles, i == currentStop)
                    Divider(color = Color.LightGray, thickness = 0.5.dp)
                }
            }
        }
    }

    @Composable
    fun StopsList(stops: List<Stop>, isMiles: Boolean) {
        var totalDistanceCovered = 0.0
        var totalDistanceRemaining = 0.0
        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
//            ProgressBar(progressState)  // Display the progress bar
            Column {
                for (i in stops.indices) {
                    val stop = stops[i]
                    totalDistanceRemaining += stop.distanceInMiles

                    val isCurrentStop = i == currentStop
                    if (isCurrentStop) {
                        totalDistanceCovered = calculateTotalDistanceCovered(stops, currentStop, isMiles)
                        totalDistanceRemaining = calculateTotalDistanceRemaining(stops, currentStop, isMiles)
                    }
                    StopCard(stop, isMiles, isCurrentStop)
                    Divider(color = Color.LightGray, thickness = 0.5.dp)
                }
            }
        }
        // Automatically scroll to the current stop
        LaunchedEffect(key1 = currentStop) {
            // Calculate the scroll position based on the index and the height of each item
            val scrollPosition = currentStop * 250  // value obtained from trial and error
            scrollState.scrollTo(scrollPosition)
        }

        // Update text views
        val unit = if (isMiles) "miles" else "km"
        tvTotalDistanceCovered.text = "Total Distance Covered: ${totalDistanceCovered.round(2)} $unit"
        tvTotalDistanceRemaining.text = "Total Distance Remaining: ${totalDistanceRemaining.round(2)} $unit"

    }

    @Composable
    fun LazyStopsList(stops: List<Stop>, isMiles: Boolean) {
        var totalDistanceCovered = 0.0
        var totalDistanceRemaining = 0.0
        val listState = rememberLazyListState()

//        ProgressBar(progressState) // Display the progress bar

        LazyColumn(state = listState) {
            itemsIndexed(stops) { index, stop ->
                totalDistanceRemaining += stop.distanceInMiles

                val isCurrentStop = index == currentStop
                if (isCurrentStop) {
                    totalDistanceCovered = calculateTotalDistanceCovered(stops, currentStop, isMiles)
                    totalDistanceRemaining = calculateTotalDistanceRemaining(stops, currentStop, isMiles)
                }
                StopCard(stop, isMiles, isCurrentStop)
                Divider(color = Color.LightGray, thickness = 0.5.dp)
            }
        }
        // Automatically scroll to the current stop
        LaunchedEffect(key1 = currentStop) {
            listState.animateScrollToItem(currentStop)
        }
        // Update text views
        val unit = if (isMiles) "miles" else "km"
        tvTotalDistanceCovered.text = "Total Distance Covered: ${totalDistanceCovered.round(2)} $unit"
        tvTotalDistanceRemaining.text = "Total Distance Remaining: ${totalDistanceRemaining.round(2)} $unit"

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
        for (i in 0 until currentStop) {
            totalDistance += if (isMiles) stops[i].distanceInMiles else stops[i].distanceInKm
        }
        return totalDistance
    }

    private fun calculateTotalDistanceRemaining(stops: List<Stop>, currentStop: Int, isMiles: Boolean): Double {
        var totalDistance = 0.0
        for (i in currentStop until stops.size) {
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
        progressState = (currentStop.toFloat() / totalStops.toFloat())
    }

    @Composable
    fun AppLogo() {
        val image = painterResource(id = R.drawable.journeytracklogo)
        Image(painter = image, contentDescription = "My Image", modifier = Modifier.size(400.dp))
    }
}
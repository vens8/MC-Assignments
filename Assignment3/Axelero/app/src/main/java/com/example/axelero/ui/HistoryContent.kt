package com.example.axelero.ui

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.axelero.db.OrientationData
import com.example.axelero.repository.OrientationDataRepository
import com.example.axelero.ui.components.LineChart
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryContent(
    orientationDataRepository: OrientationDataRepository,
    createDocumentResult: ActivityResultLauncher<Intent>
) {
    val context = LocalContext.current
    val orientationData = produceState<List<OrientationData>>(initialValue = emptyList()) {
        value = orientationDataRepository.getOrientationData()
    }
    val modelProducer1 = remember { CartesianChartModelProducer.build() }
    val modelProducer2 = remember { CartesianChartModelProducer.build() }
    val modelProducer3 = remember { CartesianChartModelProducer.build() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Orientation Data History") },
                navigationIcon = {
                    IconButton(onClick = {(context as? Activity)?.onBackPressed() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            OutlinedCard(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.outlinedCardElevation(defaultElevation = 4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("X Angle", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    LineChart("X Angle", orientationData.value.map { it.xAngle }, modelProducer1)
                }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedCard(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.outlinedCardElevation(defaultElevation = 4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Y Angle", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    LineChart("Y Angle", orientationData.value.map { it.yAngle }, modelProducer2)
                }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedCard(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.outlinedCardElevation(defaultElevation = 4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Z Angle", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    LineChart("Z Angle", orientationData.value.map { it.zAngle }, modelProducer3)
                }
            }

            Spacer(Modifier.height(24.dp))
            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TITLE, "orientation_data_" + orientationDataRepository.sensingInterval.toString() + ".txt")
                    }
                    createDocumentResult.launch(intent)
                },
                modifier = Modifier.fillMaxWidth(0.6f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Export Data")
            }
            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        orientationDataRepository.clearOrientationData()
                    }
                },
                modifier = Modifier.fillMaxWidth(0.6f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Reset Data")
            }
        }
    }
}



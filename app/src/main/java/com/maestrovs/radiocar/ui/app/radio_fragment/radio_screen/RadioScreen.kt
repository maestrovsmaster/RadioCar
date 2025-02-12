package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.maestrovs.radiocar.data.repository.mock.FakeStationRepository

import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.MiniPlayerWidget
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.RadioViewModel

/**
 * Created by maestromaster on 10/02/2025.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioScreen(
    viewModel: RadioViewModel
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Compose Fragment") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Hello from Compose!", fontSize = 24.sp)

            Box(modifier = Modifier.fillMaxSize().background(color = Color.Yellow)) {
                // Основний UI

                // Плеєр внизу
                MiniPlayerWidget(viewModel = viewModel)
            }
        }
    }
}

@Composable
@Preview
fun RadioScreenPreview(){

    RadioScreen(
        RadioViewModel(
            FakeStationRepository()
        )
    )
}

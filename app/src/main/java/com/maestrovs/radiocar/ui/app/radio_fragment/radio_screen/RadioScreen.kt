package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen

import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maestrovs.radiocar.data.repository.mock.FakeStationRepository
import com.maestrovs.radiocar.manager.PlayerStateManager
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.MediumPlayerWidget

import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.MiniPlayerWidget
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery.widgets.StationsListWidget
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.RadioViewModel
import com.maestrovs.radiocar.ui.app.radio_fragment.visualizer.AudioVisualizerScreen
import com.maestrovs.radiocar.ui.app.ui.theme.primary

/**
 * Created by maestromaster on 10/02/2025.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioScreen(
    viewModel: RadioViewModel
) {

    val playerState by PlayerStateManager.playerState.collectAsStateWithLifecycle()


    Scaffold(
        containerColor = Color.Black,
     //   topBar = { TopAppBar(title = { Text("Compose Fragment") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Плеєр внизу
            MediumPlayerWidget(viewModel = viewModel)

            StationsListWidget(RadioViewModel(
                FakeStationRepository()
            ))

                // Основний UI
              //  AudioVisualizerScreen(audioSessionId: Int)





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

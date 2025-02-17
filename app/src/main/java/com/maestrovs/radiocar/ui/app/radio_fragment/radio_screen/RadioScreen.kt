package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.maestrovs.radiocar.data.repository.mock.FakeStationRepository
import com.maestrovs.radiocar.manager.radio.PlayerStateManager
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.applications.ApplicationsWidget
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery.StationsListWidget
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer.MediumPlayerWidget
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.radiodriver.RadioDriverWidget

import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.RadioViewModel

/**
 * Created by maestromaster on 10/02/2025.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioScreen(
    viewModel: RadioViewModel,
    navController: NavController,
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

        ) {


            RadioDriverWidget(viewModel = viewModel)


            Row(modifier = Modifier.fillMaxWidth()) {
                MediumPlayerWidget(viewModel = viewModel)
                //Spacer(modifier = Modifier.requiredWidth(16.dp)) // можна додати відстань між A та B
                ApplicationsWidget(navController , modifier = Modifier.weight(1f))
            }



            StationsListWidget(modifier = Modifier//align(Alignment.BottomCenter),
                ,

                RadioViewModel(
                FakeStationRepository()
            ))



        }
    }
}

@Composable
@Preview
fun RadioScreenPreview(){

    RadioScreen(
        RadioViewModel(
            FakeStationRepository()
        ), NavController(LocalContext.current)
    )
}

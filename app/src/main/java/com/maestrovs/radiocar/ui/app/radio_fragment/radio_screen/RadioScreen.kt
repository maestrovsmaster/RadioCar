package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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

    Scaffold(
        containerColor = Color.Black,
    ) { padding ->


        val configuration = LocalConfiguration.current

        val isPortrait =
            configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

        if (isPortrait) {
            VerticalOrientation(padding, viewModel, navController)
        } else {
            HorizontalOrientation(padding, viewModel, navController)
        }


    }
}


@Composable
fun VerticalOrientation(
    padding: PaddingValues,
    viewModel: RadioViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp),

        ) {
        RadioDriverWidget(
            viewModel = viewModel, modifier = Modifier
                .weight(1f)
                .padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
        )

        Row(modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)) {
            ApplicationsWidget(navController, modifier = Modifier.width(120.dp)
                .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 8.dp))

            MediumPlayerWidget(
                viewModel = viewModel,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp, bottom = 8.dp, start = 8.dp, end = 16.dp)
            )

        }

        StationsListWidget(
            viewModel,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
        )
    }
}


@Composable
fun HorizontalOrientation(
    padding: PaddingValues,
    viewModel: RadioViewModel,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp),

        ) {
        ApplicationsWidget(
            navController, modifier = Modifier
                .width(110.dp)
                .fillMaxHeight()
                .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 8.dp)
        )


        MediumPlayerWidget(
            viewModel = viewModel, modifier = Modifier
                .fillMaxHeight()
                .width(250.dp)
                .padding(vertical = 16.dp, horizontal = 8.dp)
        )


        Column(modifier = Modifier.fillMaxWidth()) {
            RadioDriverWidget(
                viewModel = viewModel,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 16.dp, bottom = 8.dp, start = 8.dp, end = 16.dp)
            )
            StationsListWidget(

                viewModel,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 16.dp, start = 8.dp, end = 16.dp)
            )
        }


    }
}
/*

@Composable
@Preview(name = "Portrait Mode", widthDp = 360, heightDp = 640)
fun RadioScreenPreviewPortrait() {
    RadioScreen(
        RadioViewModel(
            FakeStationRepository()
        ), NavController(LocalContext.current)
    )
}

@Composable
@Preview(name = "Landscape Mode", widthDp = 640, heightDp = 360)
fun RadioScreenPreviewLandscape() {
    RadioScreen(
        RadioViewModel(
            FakeStationRepository()
        ), NavController(LocalContext.current)
    )
}
*/
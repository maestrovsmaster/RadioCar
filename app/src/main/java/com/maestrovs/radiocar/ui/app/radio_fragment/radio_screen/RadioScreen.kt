package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.applications.ApplicationsWidget
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery.StationsListWidget
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer.MediumPlayerWidget
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.radiodriver.RadioDriverWidget
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.RadioViewModel

import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.maestrovs.radiocar.data.repository.MockWeatherRepository
import com.maestrovs.radiocar.data.repository.mock.MockStationRepository
import com.maestrovs.radiocar.manager.location.LocationStateManager
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.WeatherViewModel
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.repositories.SharedPreferencesRepositoryMock


/**
 * Created by maestromaster on 10/02/2025.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioScreen(
    viewModel: RadioViewModel,
    weatherViewModel: WeatherViewModel,
    navController: NavController,
    onSelectAllClick: () -> Unit = {},
) {


    Scaffold(
        containerColor = Color.Black,
    ) { padding ->


        val configuration = LocalConfiguration.current

        val isPortrait =
            configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

        if (isPortrait) {
            RadioVerticalOrientation(
                padding,
                viewModel,
                weatherViewModel,
                navController,
                onSelectAllClick
            )
        } else {
            RadioHorizontalOrientation(
                padding,
                viewModel,
                weatherViewModel,
                navController,
                onSelectAllClick
            )
        }


    }
}


@Composable
fun RadioVerticalOrientation(
    padding: PaddingValues,
    viewModel: RadioViewModel,
    weatherViewModel: WeatherViewModel,
    navController: NavController,
    onSelectAllClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp),

        ) {
        RadioDriverWidget(
            viewModel = viewModel,
            weatherViewModel = weatherViewModel,
            modifier = Modifier
                .weight(1f)
                .padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.5f)
        ) {
            ApplicationsWidget(
                navController, modifier = Modifier
                    .width(120.dp)
                    .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 8.dp)
            )

            MediumPlayerWidget(
                viewModel = viewModel,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp, bottom = 8.dp, start = 8.dp, end = 16.dp)
            )

        }

        StationsListWidget(
            viewModel,
            modifier = Modifier
                .padding(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                .weight(1f),

            onSelectAllClick = onSelectAllClick, navController
        )
    }
}


@Composable
fun RadioHorizontalOrientation(
    padding: PaddingValues,
    viewModel: RadioViewModel,
    weatherViewModel: WeatherViewModel,
    navController: NavController,
    onSelectAllClick: () -> Unit = {},
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
                weatherViewModel = weatherViewModel,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 16.dp, bottom = 8.dp, start = 8.dp, end = 16.dp)
            )
            StationsListWidget(

                viewModel,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp, bottom = 16.dp, start = 8.dp, end = 16.dp),
                onSelectAllClick = onSelectAllClick, navController
            )
        }


    }
}


@Composable
@Preview(name = "Portrait Mode", widthDp = 360, heightDp = 640)
fun RadioScreenPreviewPortrait() {
    RadioScreen(
        RadioViewModel(
            MockStationRepository(), SharedPreferencesRepositoryMock()
        ),
        WeatherViewModel(MockWeatherRepository()),
        NavController(LocalContext.current)
    )
}

@Composable
@Preview(name = "Landscape Mode", widthDp = 640, heightDp = 360)
fun RadioScreenPreviewLandscape() {
    RadioScreen(
        RadioViewModel(
            MockStationRepository(), SharedPreferencesRepositoryMock()
        ),
        WeatherViewModel(MockWeatherRepository()),
        NavController(LocalContext.current)
    )
}

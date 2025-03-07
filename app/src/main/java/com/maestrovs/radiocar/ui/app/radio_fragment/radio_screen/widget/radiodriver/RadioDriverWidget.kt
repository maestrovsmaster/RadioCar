package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.radiodriver

import SevenSegmentSpeedometer
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.data.repository.MockWeatherRepository
import com.maestrovs.radiocar.data.repository.mock.MockStationRepository
import com.maestrovs.radiocar.manager.location.LocationStateManager
import com.maestrovs.radiocar.manager.radio.PlayerStateManager
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.radiodriver.widget.DigitalWeatherWidget
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.radiodriver.widget.LottieCover
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.radiodriver.widget.SpeedUnitText
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.RadioViewModel
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.DynamicShadowCard
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.LottieLoader
import com.maestrovs.radiocar.ui.app.ui.theme.primary
import com.maestrovs.radiocar.shared_managers.SettingsManager
import com.maestrovs.radiocar.shared_managers.SpeedUnit
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.radiodriver.widget.BtStatusWidget
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.radiodriver.widget.calculateLottieSpeedAnimation
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.radiodriver.widget.calculateSpeedColor
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.weather.WeatherWidget
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.weather.WeatherWidgetLocation
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.weather.WeatherWidgetMini
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.WeatherViewModel
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.repositories.SharedPreferencesRepositoryMock
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.PowerButton

/**
 * Created by maestromaster$ on 14/02/2025$.
 */

@Composable
fun RadioDriverWidget(

    viewModel: RadioViewModel,
    weatherViewModel: WeatherViewModel,
    modifier: Modifier = Modifier,
) {

    val playerState by PlayerStateManager.playerState.collectAsStateWithLifecycle()
    val locationState by LocationStateManager.locationState.collectAsStateWithLifecycle()

    val locationAvailability by LocationStateManager.locationAvailability.collectAsStateWithLifecycle()

    val displayColor = Color(0xFF10CAE5)
    val baseGray = Color(0xFFF6F7F8)

    val speedValue = if (locationAvailability) locationState.speed.toInt() else null
    val speedColor = calculateSpeedColor(LocalContext.current, locationState.speed)

    val animationSpeed = calculateLottieSpeedAnimation(locationState.speed)

    when (SettingsManager.getSpeedUnit(LocalContext.current)) {
        SpeedUnit.kmh -> LocalContext.current.getString(R.string.km_h)
        SpeedUnit.mph -> LocalContext.current.getString(R.string.mph)
    }
    val context = LocalContext.current

    fun onCloseApp() {
        //Finish application process
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(1)
    }

    fun onLaunchBluetoothSettingsIntent() {
        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
        context.startActivity(intent)
    }


    Box(
        modifier = modifier
    ) {

        DynamicShadowCard(
            modifier = Modifier,
            // .padding(16.dp)
            contentColor = primary,
        ) {
            LottieLoader(speed = animationSpeed)

            //BackgroundCover()

            LottieCover()


            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                WeatherWidgetMini(
                    weatherViewModel,
                    modifier = Modifier,
                    // .padding(16.dp),
                    color = baseGray
                )
                Spacer(modifier = Modifier.weight(1f))

                BtStatusWidget(
                    viewModel,

                    {
                        onLaunchBluetoothSettingsIntent()
                    },
                    color = baseGray,
                )
                Spacer(modifier = Modifier.width(8.dp))
                PowerButton({
                    onCloseApp()
                })
            }


            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(start = 30.dp, top = 32.dp),

                ) {
                SevenSegmentSpeedometer(speedValue,  speedColor)
            }

            WeatherWidgetLocation(
                viewModel = weatherViewModel,  color = Color.Cyan,
                modifier = Modifier
                    .align(Alignment.BottomStart).padding(16.dp)
            )
        }


    }


}

@Preview
@Composable
fun RadioDriverWidgetPreview() {
    RadioDriverWidget(
        viewModel = RadioViewModel(
            MockStationRepository(),
            SharedPreferencesRepositoryMock()
        ),
        weatherViewModel = WeatherViewModel(MockWeatherRepository())
    )
}




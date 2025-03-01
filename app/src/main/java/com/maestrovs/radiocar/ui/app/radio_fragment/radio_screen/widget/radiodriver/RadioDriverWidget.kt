package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.radiodriver

import SevenSegmentSpeedometer
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.repositories.SharedPreferencesRepositoryMock
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.PowerButton

/**
 * Created by maestromaster$ on 14/02/2025$.
 */

@Composable
fun RadioDriverWidget(

    viewModel: RadioViewModel,
    modifier: Modifier = Modifier,
) {

    val playerState by PlayerStateManager.playerState.collectAsStateWithLifecycle()
    val locationState by LocationStateManager.locationState.collectAsStateWithLifecycle()

    val locationAvailability by LocationStateManager.locationAvailability.collectAsStateWithLifecycle()

    val displayColor = Color(0xFF10CAE5)

    val speedValue = if(locationAvailability) locationState.speed.toInt() else null

    when (SettingsManager.getSpeedUnit(LocalContext.current)) {
        SpeedUnit.kmh -> LocalContext.current.getString(R.string.km_h)
        SpeedUnit.mph -> LocalContext.current.getString(R.string.mph)
    }
    val context = LocalContext.current

    fun onCloseApp(){
        //Finish application process
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(1)
    }

    fun onLaunchBluetoothSettingsIntent(){
        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
        context.startActivity(intent)
    }


    Box(
        modifier = modifier
            //.fillMaxWidth()
            //.height(280.dp)
            //.background(Color.Black)
            //.padding(16.dp),
        //contentAlignment = Alignment.Center
    ) {

        DynamicShadowCard(
            modifier = Modifier
                // .padding(16.dp)
                , contentColor = primary,
        ) {
            LottieLoader()

            //BackgroundCover()

            LottieCover()
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(start = 30.dp, top = 40.dp),

                ) {
                SevenSegmentSpeedometer(speedValue, "mph", Color.White, )
            }

          /*  Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),

                ) {
                SpeedUnitText(unit = "Ukraine", color = displayColor, fontSize = 14.sp)
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),

                ) {
                DigitalWeatherWidget(-14.0, "C", color = displayColor)
            }*/

            Row(modifier = Modifier
                .align(Alignment.TopEnd).padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
                ){
                BtStatusWidget(viewModel, {
                    onLaunchBluetoothSettingsIntent()
                })
                Spacer(modifier = Modifier.width(8.dp))
                PowerButton({
                    onCloseApp()
                })
            }


        }
    }


}

@Preview
@Composable
fun RadioDriverWidgetPreview() {
    RadioDriverWidget(
        viewModel = RadioViewModel(
            MockStationRepository(),  SharedPreferencesRepositoryMock()
        )
    )
}




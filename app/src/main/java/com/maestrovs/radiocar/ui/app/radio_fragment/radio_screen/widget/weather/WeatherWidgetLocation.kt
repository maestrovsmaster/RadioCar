package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.weather

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.data.remote.weather.convertCelsiumToFahrenheit
import com.maestrovs.radiocar.data.remote.weather.getTemperatureWithUnit
import com.maestrovs.radiocar.data.repository.MockWeatherRepository
import com.maestrovs.radiocar.manager.location.LocationStateManager
import com.maestrovs.radiocar.shared_managers.SettingsManager
import com.maestrovs.radiocar.shared_managers.TemperatureUnit
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.radiodriver.widget.DigitalWeatherWidget
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.WeatherViewModel

/**
 * Created by maestromaster$ on 02/03/2025$.
 */
@Composable
fun WeatherWidgetLocation(
    viewModel: WeatherViewModel,
    color: Color = Color.Cyan,
    modifier: Modifier = Modifier
) {
    val weather by viewModel.weather.collectAsState()
    val locationIsAvailable by LocationStateManager.locationAvailability.collectAsState()
    val context = LocalContext.current


    val icon = when(locationIsAvailable){
        true -> R.drawable.ic_location_on
        false -> R.drawable.ic_location_off
    }


    Row(
        modifier = modifier
            .background(Color.Transparent)
            .clickable {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(id =  icon  ),
            contentDescription = "Location",
            tint = color,
            modifier = Modifier.align(Alignment.CenterVertically).width(16.dp).height(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = weather.name,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )

    }

}



@Preview
@Composable
fun WeatherWidgetLocationPreview() {
    Box(
        modifier = Modifier
            .background(Color.Black)
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        WeatherWidgetLocation(
            WeatherViewModel(MockWeatherRepository()),  color = Color.Cyan,
            modifier = Modifier
        )

    }
}

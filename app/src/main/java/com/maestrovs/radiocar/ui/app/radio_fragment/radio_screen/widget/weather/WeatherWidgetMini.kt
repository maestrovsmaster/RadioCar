package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.weather

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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
fun WeatherWidgetMini(
    viewModel: WeatherViewModel,
    color: Color = Color.Cyan,
    modifier: Modifier = Modifier
) {
    val weather by viewModel.weather.collectAsState()
    val location by LocationStateManager.locationWeatherState.collectAsState()
    val context = LocalContext.current

    Log.d("WeatherWidget", "weather: $weather, location: $location")

    // Ініціалізація погоди при старті
    LaunchedEffect(Unit) {
        viewModel.initWeather(context)
    }

    // Реактивне оновлення погоди при зміні локації
    LaunchedEffect(location) {
        location?.let {
            Log.d(
                "WeatherWidget",
                "Refreshing weather for location: ${it.latitude}, ${it.longitude}"
            )
            viewModel.refreshWeather(it.latitude, it.longitude)
        }
    }
   // val displayColor = Color(0xFF10CAE5)

    val temperatureWithUnit = getTemperatureWithUnit(context, weather.main.temp)

    Row(
        modifier = modifier
            .background(Color.Transparent)
           // .padding(16.dp)
            .clickable {
                if (LocationStateManager.locationWeatherState.value == null) return@clickable
                viewModel.refreshWeather(
                    LocationStateManager.locationState.value.latitude,
                    LocationStateManager.locationState.value.longitude
                )
            },
        verticalAlignment = Alignment.Bottom
    ) {


        WeatherIconMini(iconId = weather.weather.firstOrNull()?.icon ?: "01d")
        Spacer(modifier = Modifier.width(10.dp))
        DigitalWeatherWidget(temperature = temperatureWithUnit.first.toDouble(),
            unit = temperatureWithUnit.second.name,
            color = color)



    }

}

@Composable
fun WeatherIconMini(iconId: String) {
    val iconUrl = "https://openweathermap.org/img/wn/${iconId}@2x.png"
    AsyncImage(
        model = iconUrl,
        contentDescription = "Weather icon",
        modifier = Modifier
            .height(36.dp)
            .width(36.dp)
    )
}


@Preview
@Composable
fun WeatherWidgetMiniPreview() {
    Box(
        modifier = Modifier
            .background(Color.Black)
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        WeatherWidgetMini(
            WeatherViewModel(MockWeatherRepository()),  color = Color.Cyan,
            modifier = Modifier
        )

    }
}

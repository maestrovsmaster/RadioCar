package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.weather

import android.util.Log
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.maestrovs.radiocar.data.repository.MockWeatherRepository
import com.maestrovs.radiocar.manager.location.LocationStateManager
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.WeatherViewModel

/**
 * Created by maestromaster$ on 02/03/2025$.
 */
@Composable
fun WeatherWidget(viewModel: WeatherViewModel, modifier: Modifier = Modifier) {
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
            Log.d("WeatherWidget", "Refreshing weather for location: ${it.latitude}, ${it.longitude}")
            viewModel.refreshWeather(it.latitude, it.longitude)
        }
    }

    Card(
        modifier = Modifier
            .clickable {
                if (LocationStateManager.locationWeatherState.value == null) return@clickable
                viewModel.refreshWeather(
                    LocationStateManager.locationState.value.latitude,
                    LocationStateManager.locationState.value.longitude
                )
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = weather.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                WeatherIcon(iconId = weather.weather.firstOrNull()?.icon ?: "01d")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${weather.main.temp}°C",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = weather.weather.firstOrNull()?.description ?: "Unknown", fontSize = 16.sp)
        }
    }
}

@Composable
fun WeatherIcon(iconId: String) {
    val iconUrl = "https://openweathermap.org/img/wn/${iconId}@2x.png"
    AsyncImage(
        model = iconUrl,
        contentDescription = "Weather icon",
        modifier = Modifier
            .height(48.dp)
            .width(48.dp)
    )
}


@Preview
@Composable
fun WeatherWidgetPreview() {
    WeatherWidget(WeatherViewModel(MockWeatherRepository()))
}

package com.maestrovs.radiocar.data.entities.weather

data class WeatherResponse(val name: String, // City name
                           val main: MainInfo, // Main weather information
                           val weather: List<Weather>, // Weather description and icon
                           val rain: Rain?, // Rain information (if available)
                           val snow: Snow? // Snow information (if available)
                             )

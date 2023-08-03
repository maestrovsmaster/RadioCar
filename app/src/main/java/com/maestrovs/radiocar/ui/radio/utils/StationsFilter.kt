package com.maestrovs.radiocar.ui.radio.utils

import com.maestrovs.radiocar.data.entities.radio.Station


fun filterAll(list: List<Station>) = filterByNames(filterByRu(list))

/**
 * Blacklist
 */
fun filterByRu(list: List<Station>) = list.filterNot { it.countrycode in listOf("RU") }

/**
 * Blacklist
 */
fun filterByNames(list: List<Station>) = list.filterNot { station ->
    listOf(
        "Луганск",
        "Донецк",
        "ЛНР",
        "Новороссия",
        "ЛНР",
        "ДНР",
        "Россия",
        "РОССИЯ",
        "МОСКВА",
        "МОСКВА FM",
        "Москва"
    ).any { it in station.name } || station.countrycode == "RU"
}

fun removeStationsByCountryCode(stations: List<Station>, code: String): List<Station> {
    return stations.filter { station ->
        station.countrycode != code
    }
}

fun removeStationsByNames(stations: List<Station>, names: List<String>): List<Station> {
    return stations.filter { station ->
        station.name !in names
    }
}


fun removeStationsByFieldName(
    stations: List<Station>,
    fieldName: String,
    value: String
): List<Station> {
    return stations.filter { station ->
        when (fieldName) {
            "name" -> station.name != value
            //"code" -> station.code != value
            else -> throw IllegalArgumentException("Invalid field name: $fieldName")
        }
    }
}
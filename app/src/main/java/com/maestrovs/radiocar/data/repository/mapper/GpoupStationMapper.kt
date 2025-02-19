package com.maestrovs.radiocar.data.repository.mapper

import android.util.Log
import com.maestrovs.radiocar.data.entities.radio.BitrateOption
import com.maestrovs.radiocar.data.entities.radio.Station
import com.maestrovs.radiocar.data.entities.radio.StationGroup
import com.maestrovs.radiocar.data.entities.radio.StationStream

/**
 * Created by maestromaster on 10/02/2025$.
 */

fun List<Station>.toGroupedStations(): List<StationGroup> {
   // Log.d("MiniPlayerWidget", "Total stations count = ${this.size}")

    //this.forEach {
    //    Log.d("MiniPlayerWidget", "toGroupedStations name: ${it.name}, serveruuid: ${it.serveruuid}")
   // }

    val groupedList = this
        .groupBy { it.serveruuid.ifEmpty { it.url_resolved } } // Групуємо спочатку за `serveruuid`
        .map { (_, stations) ->
            val primaryStation = stations.maxByOrNull { it.favicon.length } ?: stations.first()

            // Визначаємо спільну назву станції
            val baseName = findCommonSubstring(stations.map { it.name })

            // Group streams by uniq URL
            val streams = stations
                .groupBy { it.url_resolved }
                .map { (_, streamsGroup) ->
                    val firstStream = streamsGroup.first()
                    StationStream( firstStream.stationuuid, firstStream.url_resolved, BitrateOption.fromBitrate(firstStream.bitrate))
                }
                .distinctBy { it.url } // Убираємо дублікати URL

            // Вибираємо favicon (беремо перший доступний)
            val favicon = primaryStation.favicon.ifEmpty {
                stations.firstOrNull { it.favicon.isNotEmpty() }?.favicon ?: ""
            }

            var isFavorite = false
            stations.forEach { st ->
                if(st.isFavorite == 1){
                    isFavorite = true
                }
            }

            StationGroup(baseName, streams, favicon, isFavorite)
        }

    //groupedList.forEach {
    //    Log.d("MiniPlayerWidget", "groupedList name: ${it.name}, streams count: ${it.streams.size}")
   // }

    return groupedList
}




fun findCommonSubstring(names: List<String>): String {
    if (names.isEmpty()) return ""

    val sortedNames = names.sortedBy { it.length }
    val shortestName = sortedNames.first()

    var commonBase = shortestName

    for (i in shortestName.length downTo 4) {
        for (j in 0..shortestName.length - i) {
            val substring = shortestName.substring(j, j + i)
            if (names.all { it.contains(substring, ignoreCase = true) }) {
                commonBase = substring.trim()
                break
            }
        }
    }

    // Визначаємо додаткові частини
    val additionalParts = names.map { it.replace(commonBase, "").trim() }
        .filter { it.isNotEmpty() } // Відсіюємо порожні
        .groupingBy { it }
        .eachCount() // Рахуємо частоту зустрічання

    val selectedAdditional = when {
        additionalParts.isEmpty() -> "" // Якщо уточнень немає
        additionalParts.values.maxOrNull()!! > 1 -> additionalParts.maxByOrNull { it.value }!!.key // Найчастіше
        else -> additionalParts.keys.first() // Якщо всі унікальні – беремо перший
    }

    return if (selectedAdditional.isNotEmpty()) {
        "$commonBase - $selectedAdditional"
    } else {
        commonBase
    }
}




package com.maestrovs.radiocar.data.repository.filters

import com.maestrovs.radiocar.data.entities.radio.Station

/**
 * Created by maestromaster$ on 20/02/2025$.
 */

val bannedNames = listOf<String>("Russkiye", "Russian",  "Рус" )
val allowedNames = listOf("христиан", "Christian","ТрансМировое")

val excludedCountries = listOf("RU", "KP" ) //, "BY"
val excludedDomains = listOf(".ru",  ".kp" , ".su") //".by",

val filters: List<(Station) -> Boolean> = listOf(
    { station ->
        val nameLower = station.name.lowercase()

        if (bannedNames.any { banned -> nameLower.contains(banned.lowercase()) }) {
            allowedNames.any { allowed -> nameLower.contains(allowed.lowercase()) }
        } else {
            true
        }
    },
    { station -> station.countrycode !in excludedCountries },
   { station -> excludedDomains.none { domain -> station.url.contains(domain, ignoreCase = true) } },
    { station ->
        if (station.countrycode.equals("by", ignoreCase = true)) {
            if (station.tags?.contains("christian") == true) {
                true
            } else {
                station.language
                    ?.split(",")
                    ?.map { it.trim().lowercase() }
                    ?.let { languages ->
                        languages.contains("belarusian") && !languages.contains("russian")
                    } ?: false
            }
        } else true
    }






)



package com.maestrovs.radiocar.ui.country_picker.utils

import com.arpitkatiyarprojects.countrypicker.models.CountryDetails

/**
 * Created by maestromaster$ on 07/03/2025$.
 */

object CountryPickerUtils {


    fun sortCountriesByOrder(
        countryCodes: List<String>,
        countriesList: List<CountryDetails>
    ): List<CountryDetails> {
        val countryOrderMap =
            countryCodes.withIndex().associate { it.value.lowercase() to it.index }
        return countriesList.sortedBy {
            countryOrderMap[it.countryCode.lowercase()] ?: Int.MAX_VALUE
        }
    }


}
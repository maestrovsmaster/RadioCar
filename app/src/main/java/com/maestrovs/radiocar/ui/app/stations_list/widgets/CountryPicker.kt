package com.maestrovs.radiocar.ui.app.stations_list.widgets

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.arpitkatiyarprojects.countrypicker.models.CountriesListDialogDisplayProperties
import com.arpitkatiyarprojects.countrypicker.models.CountryDetails
import com.arpitkatiyarprojects.countrypicker.models.CountryPickerDialogTextStyles
import com.arpitkatiyarprojects.countrypicker.models.SelectedCountryDisplayProperties
import com.arpitkatiyarprojects.countrypicker.models.SelectedCountryProperties
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.ui.app.stations_list.widgets.country_picker.CountryPicker

/**
 * Created by maestromaster$ on 20/02/2025$.
 */

@Composable
fun CountryPickerWidget(
    defaultCountryCode: String?,
    onCountrySelected: (CountryDetails) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {




                    CountryPicker(
                        defaultCountryCode = defaultCountryCode,
                        selectedCountryDisplayProperties = SelectedCountryDisplayProperties(
                            properties = SelectedCountryProperties(
                                showCountryPhoneCode = false,
                                showCountryName = true
                            )
                        ),
                        countriesListDialogDisplayProperties = CountriesListDialogDisplayProperties(
                            textStyles = CountryPickerDialogTextStyles(
                                countryPhoneCodeTextStyle = TextStyle(color = Color.Transparent)
                            )
                        ),
                       selectedCountryColor = Color.White,
                        countriesList = sortedCountries,
                        onCountrySelected = { country ->
                            onCountrySelected(country)
                        })




            }

    }
}



package com.maestrovs.radiocar.ui.country_picker.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.arpitkatiyarprojects.countrypicker.models.CountriesListDialogDisplayProperties
import com.arpitkatiyarprojects.countrypicker.models.CountryDetails

/**
 * Created by maestromaster$ on 07/03/2025$.
 */

/**
 * Composable function that displays a list item for a country in the country selection dialog.
 * The list item shows the country flag, country name, country code (optional), and country phone code.
 *
 * @param countryItem The [CountryDetails] object containing the data for the country to be displayed.
 * @param countriesListDialogDisplayProperties A [CountriesListDialogDisplayProperties] object that provides the flag dimensions and text styles for the list item.
 * @param onCountrySelected A lambda function that is called when the country list item is clicked, which selects the country.
 */
@Composable
fun CountriesListItem(
    countryItem: CountryDetails,
    countriesListDialogDisplayProperties: CountriesListDialogDisplayProperties,
    onCountrySelected: () -> Unit
) {
    with(countriesListDialogDisplayProperties) {
        ListItem(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onCountrySelected()
            },
            leadingContent = {
                Image(
                    modifier = Modifier
                        .width(flagDimensions.width)
                        .height(flagDimensions.height)
                        .clip(flagShape),
                    painter = painterResource(id = countryItem.countryFlag),
                    contentDescription = countryItem.countryName,
                )
            },
            headlineContent = {
                Text(text = buildAnnotatedString {
                    withStyle(
                        style = (if(countryItem.countryCode == "all")  { MaterialTheme.typography.titleLarge}
                                else{
                            textStyles.countryNameTextStyle
                                ?: MaterialTheme.typography.bodyMedium
                        }
                                ).toSpanStyle()
                    ) {
                        append(countryItem.countryName)
                    }
                    if (properties.showCountryCode) {
                        append("  ")
                        append("(")
                        withStyle(
                            style = (textStyles.countryCodeTextStyle
                                ?: MaterialTheme.typography.bodyMedium).toSpanStyle()
                        ) {
                            append(countryItem.countryCode.uppercase())
                        }
                        append(")")
                    }
                })
            },
            trailingContent = {
                Text(
                    text = countryItem.countryPhoneNumberCode,
                    style = textStyles.countryPhoneCodeTextStyle
                        ?: MaterialTheme.typography.bodyMedium,
                )
            })
    }
}

package com.maestrovs.radiocar.ui.country_picker


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.arpitkatiyarprojects.countrypicker.enums.CountryListDisplayType
import com.arpitkatiyarprojects.countrypicker.models.CountriesListDialogDisplayProperties
import com.arpitkatiyarprojects.countrypicker.models.CountryDetails
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.ui.country_picker.utils.FunctionHelper.searchForCountry
import com.maestrovs.radiocar.ui.country_picker.widgets.CountriesListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Composable function for displaying a country selection dialog.
 * @param countriesList List of country details to be displayed in the dialog.
 * @param countriesListDialogDisplayProperties The [CountriesListDialogDisplayProperties] properties related to the country selection dialog, including flag dimensions and text styles.
 * @param countryListDisplayType The type of UI to use for displaying the list (BottomSheet or Dialog).
 * @param onDismissRequest Callback triggered when the dialog is dismissed.
 * @param onSelected Callback triggered when a country is selected from the dialog.
 */
@Composable
internal fun CountrySelectionList(
    countriesList: List<CountryDetails>,
    countriesListDialogDisplayProperties: CountriesListDialogDisplayProperties,
    countryListDisplayType: CountryListDisplayType,
    onDismissRequest: () -> Unit,
    onSelected: (item: CountryDetails) -> Unit,
) {

    when (countryListDisplayType) {
        CountryListDisplayType.BottomSheet -> CountryListBottomSheet(
            countriesList,
            countriesListDialogDisplayProperties,
            onDismissRequest,
            onSelected
        )

        CountryListDisplayType.Dialog -> CountryListDialog(
            countriesList,
            countriesListDialogDisplayProperties,
            onDismissRequest,
            onSelected
        )
    }
}

/**
 * Composable function to display a list of countries in a dialog.
 * @param countriesList The list of countries to display in the dialog.
 * @param countriesListDialogDisplayProperties Properties for customizing the display styles and behaviors of the list.
 * @param onDismissRequest Callback invoked when the dialog is dismissed.
 * @param onSelected Callback invoked when a country is selected from the list.
 */
@Composable
private fun CountryListDialog(
    countriesList: List<CountryDetails>,
    countriesListDialogDisplayProperties: CountriesListDialogDisplayProperties,
    onDismissRequest: () -> Unit,
    onSelected: (item: CountryDetails) -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        content = {
            CountrySelectionList(
                countriesList = countriesList,
                countriesListDialogDisplayProperties = countriesListDialogDisplayProperties,
                onSelected = onSelected,
                onDismissRequest = onDismissRequest
            )
        }
    )
}


/**
 * Composable function to display a list of countries in a modal bottom sheet.
 * @param countriesList The list of countries to display in the bottom sheet.
 * @param countriesListDialogDisplayProperties Properties for customizing the display styles and behaviors of the list.
 * @param onDismissRequest Callback invoked when the bottom sheet is dismissed.
 * @param onSelected Callback invoked when a country is selected from the list.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CountryListBottomSheet(
    countriesList: List<CountryDetails>,
    countriesListDialogDisplayProperties: CountriesListDialogDisplayProperties,
    onDismissRequest: () -> Unit,
    onSelected: (item: CountryDetails) -> Unit
) {
    ModalBottomSheet(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize(),
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = null
    ) {
        CountrySelectionList(
            countriesList = countriesList,
            countriesListDialogDisplayProperties = countriesListDialogDisplayProperties,
            onSelected = onSelected,
            onDismissRequest = onDismissRequest
        )
    }
}


/**
 * Composable function to display the country selection list with a search bar and filtered results.
 * @param countriesList The complete list of country details to display.
 * @param countriesListDialogDisplayProperties Properties for customizing the display style and behavior of the country list.
 * @param onDismissRequest Callback invoked when the dismiss action (e.g., back button) is triggered.
 * @param onSelected Callback invoked when a country is selected from the list.
 */
@Composable
private fun CountrySelectionList(
    countriesList: List<CountryDetails>,
    countriesListDialogDisplayProperties: CountriesListDialogDisplayProperties,
    onDismissRequest: () -> Unit,
    onSelected: (item: CountryDetails) -> Unit
) {
    var searchValue by remember { mutableStateOf("") }
    var filteredCountries by remember { mutableStateOf(countriesList) }
    val coroutineScope = rememberCoroutineScope()
    Scaffold(topBar = {
        CountriesListTopBarSection(
            searchValue,
            countriesListDialogDisplayProperties,
            onDismissRequest
        ) { searchStr ->
            searchValue = searchStr
            coroutineScope.launch(Dispatchers.Default) {
                val filteredCountryList =
                    countriesList.searchForCountry(searchStr)
                withContext(Dispatchers.Main) {
                    filteredCountries = filteredCountryList
                }
            }
        }
    }) {
        CountriesListSection(
            countriesList = if (searchValue.isEmpty()) countriesList else filteredCountries,
            countriesListDialogDisplayProperties = countriesListDialogDisplayProperties,
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            onSelected = onSelected
        )
    }
}

/**
 * Composable function that displays a top bar for the country list screen.
 *
 * Features:
 * - Displays a title or a search bar based on the state.
 * - Provides navigation and search action icons.
 * - Manages focus and search interactions.
 *
 * @param searchValue The current search query string.
 * @param countriesListDialogDisplayProperties Properties for customizing the display styles and behaviors.
 * @param onDismissRequest Callback invoked when the dismiss action (e.g., back button) is triggered.
 * @param onSearchChanged Callback invoked whenever the search query changes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CountriesListTopBarSection(
    searchValue: String,
    countriesListDialogDisplayProperties: CountriesListDialogDisplayProperties,
    onDismissRequest: () -> Unit,
    onSearchChanged: (searchStr: String) -> Unit
) {
    var isSearchEnabled by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var isFocusRequested by remember {
        mutableStateOf(false)
    }
    with(countriesListDialogDisplayProperties) {
        CenterAlignedTopAppBar(
            windowInsets = WindowInsets(top = 0.dp, bottom = 0.dp),
            title = {
                if (isSearchEnabled) {
                    TextField(
                        singleLine = true,
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .fillMaxWidth()
                            .onPlaced {
                                if (!isFocusRequested) {
                                    focusRequester.requestFocus()
                                    isFocusRequested = true
                                }
                            },
                        value = searchValue,
                        onValueChange = { searchStr ->
                            onSearchChanged(searchStr)
                        },
                        placeholder = {
                            Text(
                                text = stringResource(com.arpitkatiyarprojects.countrypicker.R.string.search_country),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = textStyles.searchBarHintTextStyle ?: LocalTextStyle.current
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            disabledContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        textStyle = MaterialTheme.typography.labelLarge,
                    )
                } else {
                    Text(
                        modifier = Modifier.offset(y = (-2).dp).clickable {
                            isSearchEnabled = !isSearchEnabled
                            if (isFocusRequested) isFocusRequested = false
                            if (!isSearchEnabled) {
                                onSearchChanged("")
                            }
                        }
                        ,
                        text = stringResource(R.string.select_country),
                        style = textStyles.titleTextStyle ?: MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = {
                    onDismissRequest()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null,
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    isSearchEnabled = !isSearchEnabled
                    if (isFocusRequested) isFocusRequested = false
                    if (!isSearchEnabled) {
                        onSearchChanged("")
                    }
                }) {
                    Icon(
                        imageVector = if (isSearchEnabled) Icons.Default.Clear else Icons.Default.Search,
                        contentDescription = null,
                    )
                }
            },
        )
    }
}

/**
 * Composable function to display a list of countries in a scrollable section.
 *
 * @param countriesList The list of country details to display.
 * @param countriesListDialogDisplayProperties Properties that define the display style and behavior of the country list items.
 * @param modifier Modifier to customize the appearance and layout of the LazyColumn.
 * @param onSelected Callback function invoked when a country item is selected.
 */
@Composable
private fun CountriesListSection(
    countriesList: List<CountryDetails>,
    countriesListDialogDisplayProperties: CountriesListDialogDisplayProperties,
    modifier: Modifier = Modifier,
    onSelected: (item: CountryDetails) -> Unit
) {
    with(countriesListDialogDisplayProperties) {

        Column(modifier = modifier) {

            LazyColumn {

                if (countriesList.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(com.arpitkatiyarprojects.countrypicker.R.string.no_country_found),
                            style = textStyles.noSearchedCountryAvailableTextStyle
                                ?: MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    items(countriesList, key = { it.countryCode }) { countryItem ->
                        CountriesListItem(
                            countryItem = countryItem,
                            countriesListDialogDisplayProperties = this@with
                        ) {
                            onSelected(countryItem)
                        }
                    }
                }
            }
        }
    }
}


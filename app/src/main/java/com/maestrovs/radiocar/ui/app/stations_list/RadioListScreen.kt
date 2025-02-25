package com.maestrovs.radiocar.ui.app.stations_list

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.maestrovs.radiocar.data.repository.mock.FakeStationRepository
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer.MiniPlayerWidget
import com.maestrovs.radiocar.ui.app.stations_list.list_widget.ListWidget
import com.maestrovs.radiocar.ui.app.stations_list.search_block.SearchBlock

/**
 * Created by maestromaster$ on 19/02/2025$.
 */

@Composable
fun RadioListScreen(viewModel: RadioListViewModel,   onBackClick: () -> Unit = {},) {

    Scaffold(
        containerColor = Color.Black,
    ) { padding ->


        val configuration = LocalConfiguration.current

        val isPortrait =
            configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

        if (isPortrait) {
            RadioListVerticalOrientation( padding, viewModel, onBackClick)
        } else {
            RadioListHorizontalOrientation( padding, viewModel, onBackClick)
        }


    }




}


@Composable
fun RadioListVerticalOrientation(
    padding: PaddingValues,
    viewModel: RadioListViewModel,
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp),

        ) {

        SearchBlock(viewModel,
            onBackClick = onBackClick,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp).fillMaxWidth()
        )


        ListWidget(viewModel, modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
            .weight(1f))

        MiniPlayerWidget(
            viewModel,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                .height(90.dp)
        )
    }
}


@Composable
fun RadioListHorizontalOrientation(
    padding: PaddingValues,
    viewModel: RadioListViewModel,
    onBackClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp),

        ) {



        Column(modifier = Modifier.fillMaxHeight().width(340.dp)) {
            SearchBlock(viewModel,
                onBackClick = onBackClick,
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 8.dp).fillMaxWidth()
            )
            MiniPlayerWidget(
                viewModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 8.dp)
                    .fillMaxHeight()
            )
        }

        ListWidget(viewModel, modifier =
        Modifier.weight(1f)
            .padding(top = 16.dp, bottom = 16.dp, start = 8.dp, end = 16.dp))


    }
}





@Preview(
    name = "Portrait",
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Preview(
    name = "Landscape",
    widthDp = 640, // Розширюємо ширину для ландшафтного режиму
    heightDp = 360 // Зменшуємо висоту
)
@Composable
fun RadioListScreenPreview() {
    RadioListScreen(
        RadioListViewModel(FakeStationRepository())

    )
}

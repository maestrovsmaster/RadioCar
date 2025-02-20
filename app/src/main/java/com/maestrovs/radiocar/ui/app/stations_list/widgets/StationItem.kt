package com.maestrovs.radiocar.ui.app.stations_list.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maestrovs.radiocar.data.entities.radio.StationGroup
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer.widget.BackgroundCover

/**
 * Created by maestromaster$ on 19/02/2025$.
 */

@Composable
fun StationItem(station: StationGroup) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //  item.favicon?.let{ imgUrl ->
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
            .background(Color.LightGray)
            //.padding(bottom = 40.dp, top = 0.dp, )
        ) {

            BackgroundCover(imageUrl = station.favicon)

        }


        // }

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(text = station.name)//, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            //Text(text = station.country, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

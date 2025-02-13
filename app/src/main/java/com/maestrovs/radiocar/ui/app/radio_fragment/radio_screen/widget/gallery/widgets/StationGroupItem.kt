package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maestrovs.radiocar.data.entities.radio.StationGroup
import com.maestrovs.radiocar.data.entities.radio.StationStream
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.widgets.BackgroundCover
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.DynamicShadowCard
import com.maestrovs.radiocar.ui.app.ui.theme.primary
import kotlinx.coroutines.launch

/**
 * Created by maestromaster$ on 13/02/2025$.
 */

@Composable
fun StationGroupItem(
    item: StationGroup,
    onItemClick: (StationGroup) -> Unit
    ) {


    Box(
        modifier = Modifier.fillMaxSize()
            .clickable {
                onItemClick(item)
            }
            //.width(250.dp)
            //.height(320.dp)
            //.background(Color.Black)
            //.padding(16.dp)
            ,
        //contentAlignment = Alignment.Center
    ) {

        DynamicShadowCard(
            modifier = Modifier
                // .padding(16.dp)
                .fillMaxSize(), contentColor = Color.White, backgroundColor = primary
        ) {

            item.favicon?.let{ imgUrl ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                    //.background(Color.Green)
                    //.padding(bottom = 40.dp, top = 0.dp, )
                ) {
                    BackgroundCover(imageUrl = imgUrl)
                }


            }

            Box(
                modifier = Modifier.fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0x3C3A4A57),
                                Color(0x8F27272A),
                                Color(0xFF1E2A34)
                            ),
                            startY = 0f,
                            endY = 800f
                        )
                    )
            )

            // Назва станції (внизу від кнопок)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = item.name ?: "Unknown Station",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
                Text(
                    text = "Ukraine",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Thin,
                    color = Color.White
                )
            }




        }


    }
}

@Composable
@Preview
fun StationGroupItemPreview() {

    StationGroupItem(item = StationGroup(
        name = "Test FM",
        streams = listOf<StationStream>(),
        favicon = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSPZ-WDFZ5Pz-lBPZj9GSU2LbBSEmlTVOtRmQ&s"
    ), onItemClick = {})

}

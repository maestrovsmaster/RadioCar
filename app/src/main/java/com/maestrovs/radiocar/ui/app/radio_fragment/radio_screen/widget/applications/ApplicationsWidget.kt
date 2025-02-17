package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.applications

/**
 * Created by maestromaster$ on 16/02/2025$.
 */


import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer.widget.ControlBackground
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.DynamicShadowCard
import com.maestrovs.radiocar.ui.app.ui.theme.primary

/**
 * Created by maestromaster$ on 14/02/2025$.
 */

@Composable
fun ApplicationsWidget(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val icons = listOf(
        Triple(  R.drawable.img_phone, "Phone") {
            val intent = Intent(Intent.ACTION_DIAL) // Відкриває екран набору номера
            context.startActivity(intent)
        },
        Triple(R.drawable.img_maps, "Google Maps") {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="))
            intent.setPackage("com.google.android.apps.maps")
            context.startActivity(intent)
        },
        Triple(R.drawable.img_settings, "Settings") {
            navController.navigate(R.id.action_radioFragment_to_settingsFragment)
        }
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
            //.background(Color.Black)
            .padding(16.dp),
        //contentAlignment = Alignment.Center
    ) {

        DynamicShadowCard(
            modifier = Modifier
                // .padding(16.dp)
                .fillMaxSize(), contentColor = primary, backgroundColor = primary
        ) {
            ControlBackground()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,

            ) {
                icons.forEach { (icon, label, action) ->

                    Image(
                        painter = painterResource(id =  icon),
                        contentDescription = label,
                        modifier = Modifier.size(48.dp)
                            .weight(1f).alpha(0.7f)
                            .clickable { action() },
                    )



                }
            }
     
        }
    }


}

@Preview
@Composable
fun ApplicationsWidgetPreview() {
    ApplicationsWidget( NavController(LocalContext.current))
}




package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.radiodriver.widget

import android.bluetooth.BluetoothAdapter
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.data.repository.mock.MockStationRepository
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.RadioViewModel
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.repositories.SharedPreferencesRepositoryMock

/**
 * Created by maestromaster$ on 24/02/2025$.
 */

@Composable
fun BtStatusWidget(radioViewModel: RadioViewModel, onClick: () -> Unit = {}, color: Color = Color.White, modifier: Modifier = Modifier){

    val btState by radioViewModel.isBluetoothEnabled.collectAsState()
    val currentBtDevice by radioViewModel.currentBluetoothDevice.collectAsState()

    if(btState == null) return

    val btIcon = when(btState){
        BluetoothAdapter.STATE_OFF -> R.drawable.ic_bt_off
        BluetoothAdapter.STATE_ON ->{
            if(currentBtDevice.isNullOrEmpty()){
                R.drawable.ic_bt_on
            }else{
                R.drawable.ic_bluetooth_connected
            }
        }
        else -> R.drawable.ic_settings_bluetooth_24
    }



    val tint = when(btState){
        BluetoothAdapter.STATE_OFF -> color
        BluetoothAdapter.STATE_ON ->{
            if(currentBtDevice.isNullOrEmpty()){
                color
            }else{
                Color.White
            }
        }
        else -> color
    }

    Row(modifier = modifier.clickable { onClick() }, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id =  btIcon  ),
            contentDescription = "Next",
            tint = tint,
            modifier = Modifier.align(Alignment.CenterVertically).width(20.dp).height(20.dp)
        )
        if(currentBtDevice != null){
            Text(text = currentBtDevice!!, color = Color.White)
        }
    }
}

@Preview
@Composable
fun BtStatusWidgetPreview(){
    Box(modifier = Modifier.background(Color.Black), contentAlignment = Alignment.Center) {
        BtStatusWidget(
            RadioViewModel(MockStationRepository(), SharedPreferencesRepositoryMock() )
        )
    }
}
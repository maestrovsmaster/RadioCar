package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.radiodriver.widget

import android.bluetooth.BluetoothAdapter
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.data.repository.mock.FakeStationRepository
import com.maestrovs.radiocar.manager.bluetooth.BluetoothStateManager
import com.maestrovs.radiocar.manager.radio.PlayerStateManager
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.RadioViewModel

/**
 * Created by maestromaster$ on 24/02/2025$.
 */

@Composable
fun BtStatusWidget( radioViewModel: RadioViewModel, modifier: Modifier = Modifier){

   // val isBluetoothEnabled by BluetoothStateManager.getBluetoothEnabledState(LocalContext.current).collectAsStateWithLifecycle(null)
    //val currentBtDevice by BluetoothStateManager.currentBluetoothDevice.collectAsStateWithLifecycle(null)

    val btState by radioViewModel.isBluetoothEnabled.collectAsState()
    val currentBtDevice by radioViewModel.currentBluetoothDevice.collectAsState()
    Log.d("BtStatusWidget", "isBluetoothEnabled: $btState,  currentBtDevice = $currentBtDevice")

    if(btState == null) return


       // BluetoothStateManager.getCurrentBluetoothConnected(LocalContext.current)


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

    val baseGray = Color(0xFFC6CFD5)

    val tint = when(btState){
        BluetoothAdapter.STATE_OFF -> baseGray
        BluetoothAdapter.STATE_ON ->{
            if(currentBtDevice.isNullOrEmpty()){
                baseGray
            }else{
                Color.White
            }
        }
        else -> baseGray
    }



    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
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
            RadioViewModel(FakeStationRepository())
        )
    }
}
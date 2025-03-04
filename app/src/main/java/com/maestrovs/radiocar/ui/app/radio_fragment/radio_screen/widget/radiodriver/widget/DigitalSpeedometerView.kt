import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.radiodriver.widget.SevenSegmentNumber
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.radiodriver.widget.SpeedUnitText
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.radiodriver.widget.getTSpeedWithUnit

@Composable
fun SevenSegmentSpeedometer(speed: Int?, color: Color = Color.Cyan, ) {

    val speedPair = getTSpeedWithUnit(LocalContext.current, speed?.toFloat())

    Box(
        modifier = Modifier
            //.background(Color.Black)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            SevenSegmentNumber(speedPair.first?.toInt(), color, 25, 43)
            Spacer(modifier = Modifier.width(4.dp))
            SpeedUnitText(speedPair.second.name,color, fontSize = 22.sp)
        }
    }
}



@Preview
@Composable
fun SevenSegmentSpeedometerPreview(){
    SevenSegmentSpeedometer(118,  color = Color.Cyan)
}

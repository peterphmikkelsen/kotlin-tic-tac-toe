import androidx.compose.material.Button
import androidx.compose.material.ButtonConstants
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.pressIndicatorGestureFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun ControlButton(control: ChallengeControl, size: Dp, onClick: () -> Unit) {
    if (control == ChallengeControl.ACCEPT) {
        Button(onClick = onClick, colors = ButtonConstants.defaultButtonColors(backgroundColor = Color(0, 230, 0))) {
            Icon(
                Icons.Rounded.Check.copy(defaultHeight = size, defaultWidth = size),
                tint = Color.White,
                modifier = Modifier.pressIndicatorGestureFilter(onStart = { onClick() })
            )
        }
    } else {
        Button(onClick = onClick, colors = ButtonConstants.defaultButtonColors(backgroundColor = Color(230, 0, 0))) {
            Icon(
                Icons.Rounded.Clear.copy(defaultHeight = size, defaultWidth = size),
                tint = Color.White,
                modifier = Modifier.pressIndicatorGestureFilter(onStart = { onClick() })
            )
        }
    }
}
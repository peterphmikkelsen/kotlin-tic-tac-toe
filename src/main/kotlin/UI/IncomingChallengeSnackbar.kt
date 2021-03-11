import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.socket.client.Socket

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun IncomingChallengeSnackbar(
    visible: MutableState<Boolean>,
    incomingChallenger: MutableState<String>,
    socket: Socket
) {
    AnimatedVisibility(
        visible = visible.value,
        enter = slideInVertically(initialOffsetY = { -90 }, animSpec = tween(durationMillis = 1000)),
        exit = fadeOut(animSpec = tween(durationMillis = 1000, delayMillis = 1000))
    ) {
        println(visible.value)
        Snackbar(
            modifier = Modifier.fillMaxWidth(0.95f),
            elevation = 12.dp,
            backgroundColor = Color(84, 154, 255),
            action = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End
                ) {
                    Row {
                        ControlButton(ChallengeControl.ACCEPT, 20.dp) {
                            socket.emit("challenge-accepted", incomingChallenger.value)
                            visible.value = false
                            incomingChallenger.value = ""
                        }
                        Spacer(Modifier.size(5.dp))
                        ControlButton(ChallengeControl.DECLINE, 20.dp) {
                            socket.emit("challenge-declined", incomingChallenger.value)
                            visible.value = false
                            incomingChallenger.value = ""
                        }
                    }
                }
            }) {
            if (visible.value) {
                Text("Incoming challenge from: ${incomingChallenger.value}!")
            } else {
                Text("Got it!")
            }
        }
    }
}
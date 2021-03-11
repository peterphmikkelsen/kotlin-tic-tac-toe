import androidx.compose.animation.Transition
import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.gesture.pressIndicatorGestureFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.unit.dp

@Composable
fun RotatingRefreshButton(
    board: SnapshotStateList<SnapshotStateList<String>>,
    playerInTurn: MutableState<String>,
    winner: MutableState<String>,
    hoverColor: MutableState<Color>
) {
    var flag by remember { mutableStateOf(false) }
    val rotation = FloatPropKey()
    val transitionDefinition = transitionDefinition<Int> {
        state(0) { this[rotation] = 0f }
        state(1) { this[rotation] = 360f }

        transition {
            rotation using tween(durationMillis = 500)
        }
    }
    Transition(definition = transitionDefinition, initState = 0, toState = if (flag) 1 else 0) { state ->
        Icon(
            Icons.Rounded.Refresh.copy(defaultHeight = 60.dp, defaultWidth = 60.dp),
            tint = hoverColor.value,
            modifier = Modifier.rotate(state[rotation])
                .pressIndicatorGestureFilter(
                    onStart = {
                        if (state[rotation] == 0f || state[rotation] == 360f)
                            flag = false
                    },
                    onStop = {
                        if (winner.value != "") {
                            for (l in board) l.fill("")
                            playerInTurn.value = "✕"
                            winner.value = ""
                            flag = true
                        }
                    })
                .pointerMoveFilter(
                    onEnter = {
                        if (winner.value != "")
                            hoverColor.value = Color.LightGray
                        false
                    },
                    onExit = {
                        if (winner.value != "")
                            hoverColor.value = Color.DarkGray
                        false
                    }
                )
        )
    }

}
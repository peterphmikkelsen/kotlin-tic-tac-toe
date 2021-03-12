import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.pressIndicatorGestureFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.socket.client.Socket
import kotlin.math.floor

@Composable
fun Board(
    board: SnapshotStateList<SnapshotStateList<String>>,
    playerInTurn: MutableState<String>,
    winner: MutableState<String>,
    hoverColor: MutableState<Color>,
    opponentId: MutableState<String>,
    socket: Socket
) {
    Box(
        modifier = Modifier.size(600.dp).border(10.dp, Color.Black)
            .pressIndicatorGestureFilter(onStart = {
                if (winner.value != "" && winner.value != " wins!")
                    return@pressIndicatorGestureFilter

                val x = floor(it.x/200)
                val y = floor(it.y/200)

                if (board[x.toInt()][y.toInt()] != "") return@pressIndicatorGestureFilter

                board[x.toInt()][y.toInt()] = playerInTurn.value
                socket.emit("move", opponentId.value, "$x", "$y")

                val winnerStatus = GameUtil.checkWinner(board)
                if (winnerStatus != "") {
                    winner.value = winnerStatus
                    hoverColor.value = Color.DarkGray
                }

                playerInTurn.value = if (playerInTurn.value == "✕") "○" else "✕"
            })
    ) {
        Column {
            for (i in 0 until 3) {
                Row {
                    for (j in 0 until 3) {
                        Box(modifier = Modifier.size(200.dp).border(2.dp, color = Color.Black)) {
                            Text(board[j][i],
                                fontSize = 150.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxSize(),
                                color = if (board[j][i] == "✕") Color(139,0,0) else Color(84, 154, 255)
                            )
                        }
                    }
                }
            }
        }
    }
}
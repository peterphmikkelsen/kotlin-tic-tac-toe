import androidx.compose.animation.Transition
import androidx.compose.animation.core.*
import androidx.compose.desktop.Window
import androidx.compose.foundation.ClickableText
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.gesture.pressIndicatorGestureFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*
import kotlin.math.floor

fun main() = Window(size = IntSize(1250, 900), title = "Tic-Tac-Toe") {
    val board = remember { SnapshotStateList<SnapshotStateList<String>>() }
    val playerInTurn = remember { mutableStateOf("✕") }
    val winner = remember { mutableStateOf("") }
    val uuid by remember { mutableStateOf(UUID.randomUUID()) }
    val color = Color(84, 154, 255)

    repeat(3) {
        board.add(mutableStateListOf("", "", ""))
    }

    MaterialTheme {
        Row {
            Spacer(Modifier.size(20.dp))
            Column {
                val showingText = if (winner.value == "") "Player in turn: ${playerInTurn.value}" else winner.value
                Row {
                    Box(modifier = Modifier.width(540.dp)) {
                        Text(showingText, fontSize = 42.sp, fontWeight = FontWeight.Bold)
                    }
                    RotatingRefreshButton(board, playerInTurn, winner)
                }
                Spacer(Modifier.size(20.dp))
                Board(board, playerInTurn, winner)
                Spacer(Modifier.size(20.dp))
                Row {
                    Text("Your ID: ", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    ClickableText(AnnotatedString("$uuid"), style = TextStyle(fontSize = 20.sp)) {}
                }
            }
            Box(modifier = Modifier.fillMaxHeight().padding(start = 20.dp)) {
                var textValue by remember { mutableStateOf("") }
                Column {
                    Spacer(Modifier.size(100.dp))
                    Text("Enter a friend's ID to challenge them to an epic game of Tic-Tac-Toe!",
                        fontWeight = FontWeight.Bold)

                    Row {
                        OutlinedTextField(
                            textValue,
                            onValueChange = { textValue = it },
                            label = { Text("Opponent ID", fontWeight = FontWeight.Bold) },
                            placeholder = { Text("E.g. 123e4567-e89b-12d3-a456-426614174000") },
                            modifier = Modifier.size(width = 480.dp, height = 62.dp),
                            activeColor = color,
                            singleLine = true,
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                            onImeActionPerformed = { action, softwareController ->
                                if (action == ImeAction.Done)
                                    sendChallenge()
                            }
                        )
                        Button(
                            onClick = { sendChallenge() },
                            colors = ButtonConstants.defaultButtonColors(color),
                            modifier = Modifier.size(63.dp).padding(start = 10.dp, top = 10.dp)
                        ) {
                            Icon(
                                Icons.Rounded.ArrowForward.copy(defaultHeight = 63.dp, defaultWidth = 63.dp),
                                tint = Color.White,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Board(
    board: SnapshotStateList<SnapshotStateList<String>>,
    playerInTurn: MutableState<String>,
    winner: MutableState<String>
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

                val winnerStatus = checkWinner(board)
                if (winnerStatus != "") {
                    winner.value = winnerStatus
                }

                playerInTurn.value = if (playerInTurn.value == "✕") "○" else "✕"
            })
    ) {
        Column {
            for (i in 0 until 3) {
                Row {
                    for (j in 0 until 3)
                        Square(j, i, board)
                }
            }
        }
    }
}


@Composable
fun Square(x: Int, y: Int, board: SnapshotStateList<SnapshotStateList<String>>) {
    Box(modifier = Modifier.size(200.dp).border(2.dp, color = Color.Black)) {
        Text(board[x][y], fontSize = 150.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun RotatingRefreshButton(board: SnapshotStateList<SnapshotStateList<String>>, playerInTurn: MutableState<String>, winner: MutableState<String>) {
    var flag by remember { mutableStateOf(false) }
    val rotation = FloatPropKey()
    val def = transitionDefinition<Int> {
        state(0) { this[rotation] = 0f }
        state(1) { this[rotation] = 360f }

        transition {
            rotation using repeatable(animation = tween(500), iterations = 1)
        }
    }
    Transition(definition = def, initState = 0, toState = if (flag) 1 else 0) { state ->
        Icon(
            Icons.Rounded.Refresh.copy(defaultHeight = 60.dp, defaultWidth = 60.dp),
            tint = if (winner.value != "") Color.DarkGray else Color.LightGray,
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
        )
    }

}

private fun sendChallenge() {
    // TODO: Handle challenging a friend
}

private fun checkWinner(board: SnapshotStateList<SnapshotStateList<String>>): String
{
    val wins = mutableListOf<Triple<Pair<Int, Int>, Pair<Int, Int>, Pair<Int, Int>>>()
    wins.add(Triple(Pair(0, 0), Pair(1, 0), Pair(2, 0)))
    wins.add(Triple(Pair(0, 1), Pair(1, 1), Pair(2, 1)))
    wins.add(Triple(Pair(0, 2), Pair(1, 2), Pair(2, 2)))
    wins.add(Triple(Pair(0, 0), Pair(1, 1), Pair(2, 2)))
    wins.add(Triple(Pair(0, 2), Pair(1, 1), Pair(2, 0)))
    wins.add(Triple(Pair(0, 0), Pair(0, 1), Pair(0, 2)))
    wins.add(Triple(Pair(1, 0), Pair(1, 1), Pair(1, 2)))
    wins.add(Triple(Pair(2, 0), Pair(2, 1), Pair(2, 2)))

    for ((i, j, k) in wins) {
        if (board[i.first][i.second] != "" && board[i.first][i.second] == board[j.first][j.second] && board[i.first][i.second] == board[k.first][k.second])
            return "${board[i.first][i.second]} WINS!"
    }

    if (board.all { it.all { square -> square != "" } })
        return "DRAW..."
    return ""
}
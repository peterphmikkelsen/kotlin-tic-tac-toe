import androidx.compose.desktop.Window
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.pressIndicatorGestureFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.floor

fun main() = Window(size = IntSize(1000, 800)) {
    var board = remember { SnapshotStateList<SnapshotStateList<String>>() }
    var playerInTurn = remember { mutableStateOf("X") }
    var winner = remember { mutableStateOf("") }
    repeat(3) {
        board.add(mutableStateListOf("", "", ""))
    }
    MaterialTheme {
        Row {
            Board(board, playerInTurn, winner)
            Column {
                Button(modifier = Modifier.size(100.dp, 50.dp), colors = ButtonConstants.defaultButtonColors(backgroundColor = Color.Blue), onClick = {
                    for (l in board) l.fill("")
                    playerInTurn.value = "X"
                    winner.value = ""
                }) {
                    Text(modifier = Modifier.fillMaxSize(), text = "RESET", textAlign = TextAlign.Center, fontSize = 20.sp, color = Color.White)
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
    val winningCombinations = mutableListOf<Triple<Pair<Int, Int>, Pair<Int, Int>, Pair<Int, Int>>>()
    winningCombinations.add(Triple(Pair(0, 0), Pair(1, 0), Pair(2, 0)))
    winningCombinations.add(Triple(Pair(0, 1), Pair(1, 1), Pair(2, 1)))
    winningCombinations.add(Triple(Pair(0, 2), Pair(1, 2), Pair(2, 2)))
    winningCombinations.add(Triple(Pair(0, 0), Pair(1, 1), Pair(2, 2)))
    winningCombinations.add(Triple(Pair(0, 2), Pair(1, 1), Pair(2, 0)))
    winningCombinations.add(Triple(Pair(0, 0), Pair(0, 1), Pair(0, 2)))
    winningCombinations.add(Triple(Pair(1, 0), Pair(1, 1), Pair(1, 2)))
    winningCombinations.add(Triple(Pair(2, 0), Pair(2, 1), Pair(2, 2)))

    Box(
        modifier = Modifier.size(600.dp)
            .pressIndicatorGestureFilter(onStart = {
                if (winner.value != "" && winner.value != " wins!")
                    return@pressIndicatorGestureFilter

                val x = floor(it.x/200)
                val y = floor(it.y/200)

                if (board[x.toInt()][y.toInt()] != "") return@pressIndicatorGestureFilter

                board[x.toInt()][y.toInt()] = playerInTurn.value

                val winnerStatus = checkWinner(board, winningCombinations)
                if (winnerStatus != null) {
                    winner.value = playerInTurn.value
                    return@pressIndicatorGestureFilter
                }

                playerInTurn.value = if (playerInTurn.value == "X") "O" else "X"
            })
    ) {
        Column {
            for (i in 0 until 3) {
                Row {
                    for (j in 0 until 3)
                        Square(j, i, board, winner)
                }
            }
        }
    }
}


@Composable
fun Square(x: Int, y: Int, board: SnapshotStateList<SnapshotStateList<String>>, winner: MutableState<String>) {
    Box(
        modifier = Modifier.size(200.dp)
            .border(2.dp, color = Color.Black)
    ) {
        Text(board[x][y], fontSize = 150.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxSize())
    }
}

private fun checkWinner(
    board: SnapshotStateList<SnapshotStateList<String>>,
    wins: MutableList<Triple<Pair<Int, Int>, Pair<Int, Int>, Pair<Int, Int>>>)
: Triple<Pair<Int, Int>, Pair<Int, Int>, Pair<Int, Int>>?
{
    for ((i, j, k) in wins) {
        if (board[i.first][i.second] != "" && board[i.first][i.second] == board[j.first][j.second] && board[i.first][i.second] == board[k.first][k.second])
            return Triple(Pair(i.first, i.second), Pair(j.first, j.second), Pair(k.first, k.second))
    }
    return null
}
import androidx.compose.desktop.Window
import androidx.compose.foundation.ClickableText
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.socket.client.IO
import io.socket.client.Socket
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main() = Window(size = IntSize(1250, 785), title = "Tic-Tac-Toe", icon = getWindowIcon()) {
    val board = remember { SnapshotStateList<SnapshotStateList<String>>() }
    val playerInTurn = remember { mutableStateOf("✕") }
    val winner = remember { mutableStateOf("") }
    val id = remember { mutableStateOf("") }
    val hoverColor = remember { mutableStateOf(Color.LightGray) }
    val incomingChallenger = remember { mutableStateOf("") }
    val incomingChallenge = remember { mutableStateOf(false) }
    val pendingChallengeId = remember { mutableStateOf("") }
    val challengeState = remember { mutableStateOf(ChallengeState.NONE) }
    val snackbarVisible = remember { mutableStateOf(false) }

    repeat(3) {
        board.add(mutableStateListOf("", "", ""))
    }

    val socket = IO.socket("http://localhost:3000")
    socket.on("connect") {
        id.value = socket.id()
        println("Your ID: ${id.value}")
    }
    socket.on("incoming-challenge") {
        println("Incoming challenge from: ${it[0] as String}")
        incomingChallenger.value = it[0] as String
        incomingChallenge.value = true
    }
    socket.on("challenge-accepted") {
        println("Your challenge was accepted!")
        pendingChallengeId.value = ""
        challengeState.value = ChallengeState.ACCEPTED
    }
    socket.on("challenge-declined") {
        println("Your challenge was declined...")
        pendingChallengeId.value = ""
        challengeState.value = ChallengeState.DECLINED
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
                    RotatingRefreshButton(board, playerInTurn, winner, hoverColor)
                }
                Spacer(Modifier.size(20.dp))
                Board(board, playerInTurn, winner, hoverColor)
                Spacer(Modifier.size(20.dp))
                Row {
                    Text("Your ID: ", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    ClickableText(AnnotatedString(id.value), style = TextStyle(fontSize = 20.sp)) {}
                }
            }
            Box(modifier = Modifier.fillMaxHeight().padding(start = 20.dp)) {
                val textValue = remember { mutableStateOf("") }
                Column {
                    Spacer(Modifier.size(20.dp))
                    ChallengeSnackbar(snackbarVisible, pendingChallengeId, challengeState)
                    IncomingChallengeSnackbar(incomingChallenge, incomingChallenger, socket)
                }
                Column {
                    Spacer(Modifier.size(100.dp))
                    ChallengeFriend(textValue, socket, pendingChallengeId, challengeState)
                    snackbarVisible.value = challengeState.value == ChallengeState.PENDING
                }

            }
        }
    }
    socket.connect()
}

fun Socket.sendChallenge(userId: MutableState<String>, pendingChallenge: MutableState<String>, challengeState: MutableState<ChallengeState>) {
    if (userId.value == "") return
    println("Challenging: ${userId.value}")
    this.emit("challenge", userId.value)
    pendingChallenge.value = userId.value
    challengeState.value = ChallengeState.PENDING
}

private fun getWindowIcon(): BufferedImage {
    return ImageIO.read(File("src/main/resources/tic-tac-toe-icon.png"))
        ?: BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
}

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.socket.client.Socket

@Composable
fun ChallengeFriend(
    textValue: MutableState<String>,
    socket: Socket, pendingChallenge: MutableState<String>,
    challengeState: MutableState<ChallengeState>
) {
    val isNotEmptyAndCorrectLength = textValue.value != "" && (textValue.value.length < 20 || textValue.value.length > 20)
    val isOwnId = textValue.value == socket.id()
    val challengeIsPendingOrAccepted = challengeState.value == ChallengeState.PENDING || challengeState.value == ChallengeState.ACCEPTED
    val cannotChallenge = isNotEmptyAndCorrectLength || isOwnId || challengeIsPendingOrAccepted
    Text("Enter a friend's ID to challenge them to an epic game of Tic-Tac-Toe!",
        fontWeight = FontWeight.Bold)

    Row {
        OutlinedTextField(
            textValue.value,
            onValueChange = { textValue.value = it },
            label = { Text("Opponent ID", fontWeight = FontWeight.Bold) },
            placeholder = { Text("E.g. 0yswdMPtFFCj6LztAAAH") },
            modifier = Modifier.size(width = 475.dp, height = 62.dp),
            activeColor = Color(84, 154, 255),
            singleLine = true,
            maxLines = 1,
            isErrorValue = cannotChallenge,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            onImeActionPerformed = { action, _ ->
                if (action == ImeAction.Done && !cannotChallenge)
                    socket.sendChallenge(textValue, pendingChallenge, challengeState)
            }
        )
        Spacer(Modifier.size(10.dp))
        Button(
            onClick = {
                if (!challengeIsPendingOrAccepted && !cannotChallenge)
                    socket.sendChallenge(textValue, pendingChallenge, challengeState)
            },
            colors = ButtonConstants.defaultButtonColors(
                if (!challengeIsPendingOrAccepted && !cannotChallenge)
                    Color(84, 154, 255)
                else
                    Color.LightGray
            ),
            modifier = Modifier.padding(top = 11.dp),
        ) {
            Icon(
                Icons.Rounded.ArrowForward.copy(defaultHeight = 32.dp, defaultWidth = 32.dp),
                tint = Color.White,
            )
        }
    }
}
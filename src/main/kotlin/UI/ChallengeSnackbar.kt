import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChallengeSnackbar(
    pendingChallenge: MutableState<String>,
    challengeState: MutableState<ChallengeState>
) {
    AnimatedVisibility(
        visible = challengeState.value == ChallengeState.PENDING,
        enter = slideInVertically(initialOffsetY = { -100 }, animSpec = tween(durationMillis = 1000)),
        exit = fadeOut(animSpec = tween(durationMillis = 1000, delayMillis = 1000))
    ) {
        Snackbar(
            text = {
                when (challengeState.value) {
                    ChallengeState.PENDING -> Text("Pending answer from: ${pendingChallenge.value}")
                    ChallengeState.ACCEPTED -> Text("ACCEPTED! Get ready to fight!")
                    ChallengeState.DECLINED -> Text("Your challenge was declined...")
                    else -> Text("")
                }
            },
            modifier = Modifier.fillMaxWidth(0.95f),
            elevation = 12.dp,
            backgroundColor = when (challengeState.value) {
                ChallengeState.PENDING -> Color(84, 154, 255)
                ChallengeState.ACCEPTED -> Color(0, 230, 0)
                ChallengeState.DECLINED -> Color(230, 0, 0)
                else -> Color.Transparent
            },
        )
    }
}
import androidx.compose.runtime.snapshots.SnapshotStateList

class GameUtil {
    companion object Rules {
        fun checkWinner(board: SnapshotStateList<SnapshotStateList<String>>): String {
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
    }
}
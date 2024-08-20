package tech.droi.a15puzzle

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import tech.droi.a15puzzle.Game15.Companion.SIDE

class GameViewModel: ViewModel() {
    private val game15: Game15 = Game15()
    val tiles: ArrayList<MutableState<Pair<Int, Int>>> = arrayListOf(
        mutableStateOf(Pair(0, 0)), mutableStateOf(Pair(1, 0)), mutableStateOf(Pair(2, 0)),
        mutableStateOf(Pair(3, 0)), mutableStateOf(Pair(0, 1)), mutableStateOf(Pair(1, 1)), mutableStateOf(Pair(2, 1)),
        mutableStateOf(Pair(3, 1)), mutableStateOf(Pair(0, 2)), mutableStateOf(Pair(1, 2)), mutableStateOf(Pair(2, 2)),
        mutableStateOf(Pair(3, 2)), mutableStateOf(Pair(0, 3)), mutableStateOf(Pair(1, 3)), mutableStateOf(Pair(2, 3))
    )
    val stage: MutableState<GameStage> = mutableStateOf(GameStage.EXIT)
    val steps: MutableIntState = mutableIntStateOf(0)

    init {
        game15.start()
        reData()
    }

    fun click(indexTile: Int) {
        val x: Int = tiles[indexTile - 1].value.first
        val y: Int = tiles[indexTile - 1].value.second
        game15.pushTile(x, y)
        reData()
    }

    fun start() {
        game15.start()
        reData()
    }

    private fun reData() {
        for (i in 1 until SIDE * SIDE) {
            val seek = seek(i)
            if (seek != null)
                tiles[i - 1].value = seek
        }
        stage.value = game15.stage
        steps.intValue = game15.steps
    }

    private fun seek(tile: Int): Pair<Int, Int>? {
        for (y in 0 until SIDE)
            for (x in 0 until SIDE)
                if (game15.gameField[y][x] == tile)
                    return Pair(x, y)
        return null
    }
}
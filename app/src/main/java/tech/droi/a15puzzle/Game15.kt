package tech.droi.a15puzzle

import kotlin.random.Random

class Game15 {
    val gameField: Array<IntArray> = Array(SIDE) { IntArray(SIDE) }
    var stage: GameStage = GameStage.EXIT
    var steps = 0

    fun start() {
        var tile = 1
        for (y in 0 until SIDE)
            for (x in 0 until SIDE)
                if (tile < SIDE * SIDE)
                    gameField[y][x] = tile++
                else
                    gameField[y][x] = 0
        stage = GameStage.EXIT
        shaker()
        stage = GameStage.PLAY
    }

    fun pushTile(x: Int, y:Int) {
        if (stage != GameStage.WIN && gameField[y][x] != 0) {
            if (y - 1 >= 0 && gameField[y - 1][x] == 0)
                move(Pair(x, y), Pair(x, y - 1))
            else if (y + 1 < SIDE && gameField[y + 1][x] == 0)
                move(Pair(x, y), Pair(x, y + 1))
            else if (x - 1 >= 0 && gameField[y][x - 1] == 0)
                move(Pair(x, y), Pair(x - 1, y))
            else if (x + 1 < SIDE && gameField[y][x + 1] == 0)
                move(Pair(x, y), Pair(x + 1, y))
        }
    }

    private fun move(from: Pair<Int, Int>, to: Pair<Int, Int>) {
        gameField[to.second][to.first] = gameField[from.second][from.first]
        gameField[from.second][from.first] = 0
        if (stage == GameStage.PLAY) {
            steps++
            check()
        }
    }

    private fun check() {
        var check = true
        var tile = 0
        for (y in 0..< SIDE)
            for (x in 0..< SIDE)
                check = if (x == SIDE - 1 && y == SIDE - 1)
                    check && (gameField[y][x] == 0)
                else
                    check && (gameField[y][x] == ++tile)
        if (check)
            stage = GameStage.WIN
    }

    private fun shaker() {
        for (i in 0 until SHAKER_COUNT)
            pushTile(Random.nextInt(SIDE), Random.nextInt(SIDE))
    }

    companion object {
        const val SIDE = 4
        const val SHAKER_COUNT = 10000
    }
}
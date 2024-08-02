package tech.droi.a15puzzle

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat.finishAffinity
import kotlin.random.Random

private const val SHAKER_COUNT = 1000
private const val SIZE = 4

private val gameField = arrayOf(intArrayOf(1, 2, 3, 4), intArrayOf(5, 6, 7, 8), intArrayOf(9, 10, 11, 12),
    intArrayOf(13, 14, 15, 0))
private var isClickable = false

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tiles = arrayListOf<MutableState<Pair<Int, Int>>>()
        for (i in 1..< SIZE * SIZE)
            seek(i)?.let { tiles.add(mutableStateOf(it)) }
        start(tiles)
        val showDialog = mutableStateOf(false)
        setContent {
            Greeting(tiles, showDialog)
        }
    }
}

@Composable
fun Greeting( tiles: ArrayList<MutableState<Pair<Int, Int>>>, showDialog: MutableState<Boolean> ) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val sideCard = min(screenHeight, screenWidth) - 8.dp
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Card(modifier = Modifier
            .width(sideCard)
            .height(sideCard),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box {
                val sideButton = (sideCard - 4.dp * (SIZE + 1)) / SIZE
                for (i in 1..< SIZE * SIZE) {
                    Button(
                        onClick = {
                            click(tiles, i, showDialog)
                        },
                        modifier = Modifier
                            .offset(4.dp + (sideButton + 4.dp) * tiles[i - 1].value.first,
                                4.dp + (sideButton + 4.dp) * tiles[i - 1].value.second)
                            .width(sideButton)
                            .height(sideButton),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.teal_200))
                    ) {
                        Text(
                            text = i.toString(), fontSize = 24.sp, color = colorResource(R.color.black)
                        )
                    }
                }
            }
        }
    }
    if (showDialog.value)
        Dialog(onDismissRequest = {}) {
            Card(shape = RoundedCornerShape(8.dp)) {
                Column(modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = stringResource(R.string.win), textAlign = TextAlign.Center, color = colorResource(R.color.red))
                    Row {
                        TextButton(onClick = {
                            showDialog.value = false
                            start(tiles)
                        }) {
                            Text(stringResource(R.string.play))
                        }
                        TextButton(onClick = {
                            showDialog.value = false
                            val activity = (context as? Activity)
                            if (activity != null)
                                finishAffinity(activity)
                        }) {
                            Text(stringResource(R.string.exit))
                        }
                    }
                }
            }
        }
    BackHandler {
        val activity = (context as? Activity)
        if (activity != null)
            finishAffinity(activity)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val tiles = remember { arrayListOf(mutableStateOf(Pair(0, 0)), mutableStateOf(Pair(1, 0)), mutableStateOf(Pair(2, 0)),
        mutableStateOf(Pair(3, 0)), mutableStateOf(Pair(0, 1)), mutableStateOf(Pair(1, 1)), mutableStateOf(Pair(2, 1)),
        mutableStateOf(Pair(3, 1)), mutableStateOf(Pair(0, 2)), mutableStateOf(Pair(1, 2)), mutableStateOf(Pair(2, 2)),
        mutableStateOf(Pair(3, 2)), mutableStateOf(Pair(0, 3)), mutableStateOf(Pair(1, 3)), mutableStateOf(Pair(2, 3))) }
    val showDialog = remember { mutableStateOf(true) }
    Greeting(tiles, showDialog)
}

private fun seek(tile: Int): Pair<Int, Int>? {
    for (y in 0..< SIZE)
        for (x in 0..< SIZE)
            if (gameField[y][x] == tile)
                return Pair(x, y)
    return null
}

private fun click(tiles: ArrayList<MutableState<Pair<Int, Int>>>, button: Int, showDialog: MutableState<Boolean>) {
    val seek = seek(button)
    if (seek != null) {
        val coordinate: Pair<Int, Int> = seek
        if (coordinate.first - 1 >= 0) {
            if (gameField[coordinate.second][coordinate.first - 1] == 0) {
                move(Direction.RIGHT)
                tiles[button - 1].value = Pair(coordinate.first - 1, coordinate.second)
            }
        }
        if (coordinate.first + 1 < SIZE) {
            if (gameField[coordinate.second][coordinate.first + 1] == 0) {
                move(Direction.LEFT)
                tiles[button - 1].value = Pair(coordinate.first + 1, coordinate.second)
            }
        }
        if (coordinate.second - 1 >= 0) {
            if (gameField[coordinate.second - 1][coordinate.first] == 0) {
                move(Direction.DOWN)
                tiles[button - 1].value = Pair(coordinate.first, coordinate.second - 1)
            }
        }
        if (coordinate.second + 1 < SIZE) {
            if (gameField[coordinate.second + 1][coordinate.first] == 0) {
                move(Direction.UP)
                tiles[button - 1].value = Pair(coordinate.first, coordinate.second + 1)
            }
        }
        check(showDialog)
    }
}

private fun check(showDialog: MutableState<Boolean>) {
    var check = true
    var tile = 0
    for (y in 0..< SIZE)
        for (x in 0..< SIZE)
            check = if (x == SIZE - 1 && y == SIZE - 1)
                check && (gameField[y][x] == 0)
            else
                check && (gameField[y][x] == ++tile)
    if (check)
        win(showDialog)
}

private fun win(showDialog: MutableState<Boolean>) {
    isClickable = false
    showDialog.value = true
}

private fun shaker() {
    for (i in 1..SHAKER_COUNT)
        move(Direction.fromInt(Random.nextInt(4)))
}

private fun move(direction: Direction) {
    val seek = seek(0)
    if (seek != null) {
        val coordinate: Pair<Int, Int> = seek
        when (direction) {
            Direction.UP -> {
                if (coordinate.second - 1 >= 0) {
                    gameField[coordinate.second][coordinate.first] = gameField[coordinate.second - 1][coordinate.first]
                    gameField[coordinate.second - 1][coordinate.first] = 0
                }
            }
            Direction.DOWN -> {
                if (coordinate.second + 1 < SIZE) {
                    gameField[coordinate.second][coordinate.first] = gameField[coordinate.second + 1][coordinate.first]
                    gameField[coordinate.second + 1][coordinate.first] = 0
                }
            }
            Direction.LEFT -> {
                if (coordinate.first - 1 >= 0) {
                    gameField[coordinate.second][coordinate.first] = gameField[coordinate.second][coordinate.first - 1]
                    gameField[coordinate.second][coordinate.first - 1] = 0
                }
            }
            Direction.RIGHT -> {
                if (coordinate.first + 1 < SIZE) {
                    gameField[coordinate.second][coordinate.first] = gameField[coordinate.second][coordinate.first + 1]
                    gameField[coordinate.second][coordinate.first + 1] = 0
                }
            }
        }
    }
}

private fun start(tiles: ArrayList<MutableState<Pair<Int, Int>>>) {
    shaker()
    for (i in 1..< SIZE * SIZE)
        seek(i)?.let { tiles[i - 1] = mutableStateOf(it) }
    isClickable = true
}

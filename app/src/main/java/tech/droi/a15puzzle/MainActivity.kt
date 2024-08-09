package tech.droi.a15puzzle

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat.finishAffinity
import kotlin.random.Random

private const val SHAKER_COUNT = 1000
private const val SIDE = 4
private const val PADDING = 8

private var gameField = arrayOf<IntArray>()
private var isClickable = false
private var steps = 0

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var count = 1
        for (y in 0 ..< SIDE) {
            var array = intArrayOf()
            for (x in 0..<SIDE)
                array +=
                    if (count < SIDE * SIDE)
                        count++
                    else
                        0
            gameField += array
        }
        val tiles = arrayListOf<MutableState<Pair<Int, Int>>>()
        for (i in 1..< SIDE * SIDE)
            seek(i)?.let { tiles.add(mutableStateOf(it)) }
        start(tiles)
        val showDialog = mutableStateOf(false)
        val steps = mutableIntStateOf(0)
        setContent {
            Greeting(tiles, showDialog, steps)
        }
    }
}

@Composable
fun Greeting(
    tiles: ArrayList<MutableState<Pair<Int, Int>>>,
    showDialog: MutableState<Boolean>,
    steps: MutableIntState
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val sideCard = min(screenHeight, screenWidth) - 2 * PADDING.dp
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PADDING.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .width(sideCard),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(R.string.steps_) + " ${steps.intValue}",
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
        Spacer(
            modifier = Modifier
                .height(PADDING.dp)
        )
        Card(modifier = Modifier
            .width(sideCard)
            .height(sideCard),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Box {
                val sideButton = (sideCard - PADDING.dp * (SIDE + 1)) / SIDE
                for (i in 1..< SIDE * SIDE) {
                    Button(
                        onClick = {
                            click(tiles, i, showDialog, steps)
                        },
                        modifier = Modifier
                            .offset(
                                PADDING.dp + (sideButton + PADDING.dp) * tiles[i - 1].value.first,
                                PADDING.dp + (sideButton + PADDING.dp) * tiles[i - 1].value.second
                            )
                            .width(sideButton)
                            .height(sideButton),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = i.toString(), fontSize = 24.sp, color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
    if (showDialog.value)
        Dialog(onDismissRequest = {}) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                Column(modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.win),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = 24.sp
                    )
                    Row {
                        TextButton(
                            onClick = {
                                showDialog.value = false
                                start(tiles)
                            }
                        ) {
                            Text(
                                stringResource(R.string.play),
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontSize = 24.sp
                            )
                        }
                        TextButton(
                            onClick = {
                                showDialog.value = false
                                val activity = (context as? Activity)
                                if (activity != null)
                                    finishAffinity(activity)
                            }
                        ) {
                            Text(
                                stringResource(R.string.exit),
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontSize = 24.sp
                            )
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
    val showDialog = remember { mutableStateOf(false) }
    val steps = remember { mutableIntStateOf(93) }
    Greeting(tiles, showDialog, steps)
}

private fun seek(tile: Int): Pair<Int, Int>? {
    for (y in 0..< SIDE)
        for (x in 0..< SIDE)
            if (gameField[y][x] == tile)
                return Pair(x, y)
    return null
}

private fun click(
    tiles: ArrayList<MutableState<Pair<Int, Int>>>,
    button: Int,
    showDialog: MutableState<Boolean>,
    composeSteps: MutableIntState
) {
    val seek = seek(button)
    if (isClickable && seek != null) {
        val coordinate: Pair<Int, Int> = seek
        if (coordinate.first - 1 >= 0) {
            if (gameField[coordinate.second][coordinate.first - 1] == 0) {
                move(Direction.RIGHT)
                tiles[button - 1].value = Pair(coordinate.first - 1, coordinate.second)
                composeSteps.intValue = ++steps
            }
        }
        if (coordinate.first + 1 < SIDE) {
            if (gameField[coordinate.second][coordinate.first + 1] == 0) {
                move(Direction.LEFT)
                tiles[button - 1].value = Pair(coordinate.first + 1, coordinate.second)
                composeSteps.intValue = ++steps
            }
        }
        if (coordinate.second - 1 >= 0) {
            if (gameField[coordinate.second - 1][coordinate.first] == 0) {
                move(Direction.DOWN)
                tiles[button - 1].value = Pair(coordinate.first, coordinate.second - 1)
                composeSteps.intValue = ++steps
            }
        }
        if (coordinate.second + 1 < SIDE) {
            if (gameField[coordinate.second + 1][coordinate.first] == 0) {
                move(Direction.UP)
                tiles[button - 1].value = Pair(coordinate.first, coordinate.second + 1)
                composeSteps.intValue = ++steps
            }
        }
        check(showDialog)
    }
}

private fun check(showDialog: MutableState<Boolean>) {
    var check = true
    var tile = 0
    for (y in 0..< SIDE)
        for (x in 0..< SIDE)
            check = if (x == SIDE - 1 && y == SIDE - 1)
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
                if (coordinate.second + 1 < SIDE) {
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
                if (coordinate.first + 1 < SIDE) {
                    gameField[coordinate.second][coordinate.first] = gameField[coordinate.second][coordinate.first + 1]
                    gameField[coordinate.second][coordinate.first + 1] = 0
                }
            }
        }
    }
}

private fun start(tiles: ArrayList<MutableState<Pair<Int, Int>>>) {
    shaker()
    for (i in 1..< SIDE * SIDE)
        seek(i)?.let { tiles[i - 1] = mutableStateOf(it) }
    isClickable = true
}

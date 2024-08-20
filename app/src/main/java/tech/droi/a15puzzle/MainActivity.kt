package tech.droi.a15puzzle

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.lifecycle.viewmodel.compose.viewModel
import tech.droi.a15puzzle.theme.A15Theme

private const val SIDE = 4
private const val PADDING = 8

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            A15Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel()
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val sideCard = min(screenHeight, screenWidth) - 2 * PADDING.dp
    when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            Column(
                modifier = modifier
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
                        text = stringResource(R.string.steps_) + " ${viewModel.steps.intValue}",
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
                Spacer(
                    modifier = Modifier
                        .height(PADDING.dp)
                )
                GameField(sideCard, viewModel)
            }
        }
        Configuration.ORIENTATION_LANDSCAPE -> {
            Row(
                modifier = modifier
                    .fillMaxSize()
                    .padding(PADDING.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier
                        .height(sideCard),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.steps_) + " ${viewModel.steps.intValue}",
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
                Spacer(
                    modifier = Modifier
                        .width(PADDING.dp)
                )
                GameField(sideCard, viewModel)
            }
        }
        else -> {}
    }
    if (viewModel.stage.value == GameStage.WIN)
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
                                viewModel.start()
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
    A15Theme {
        Greeting()
    }
}

@Composable
fun GameField(sideCard: Dp, viewModel: GameViewModel) {
    Card(modifier = Modifier
        .width(sideCard)
        .height(sideCard),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Box {
            val sideButton = (sideCard - PADDING.dp * (SIDE + 1)) / SIDE
            for (i in 1 until SIDE * SIDE) {
                Button(
                    onClick = {
                        viewModel.click(i)
                    },
                    modifier = Modifier
                        .offset(
                            PADDING.dp + (sideButton + PADDING.dp) * viewModel.tiles[i - 1].value.first,
                            PADDING.dp + (sideButton + PADDING.dp) * viewModel.tiles[i - 1].value.second
                        )
                        .width(sideButton)
                        .height(sideButton),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = i.toString(), color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
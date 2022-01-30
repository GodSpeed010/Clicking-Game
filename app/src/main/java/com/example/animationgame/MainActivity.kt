package com.example.animationgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.animationgame.destinations.ClickGameDestination
import com.example.animationgame.destinations.WindowsAnimationDestination
import com.example.animationgame.ui.theme.AnimationGameTheme
import com.example.animationgame.ui.theme.Blue
import com.example.animationgame.ui.theme.DarkBlue
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DestinationsNavHost(navGraph = NavGraphs.root)
        }
    }
}

@Destination(start = true)
@ExperimentalAnimationApi
@Composable
fun ClickGame(
    navigator: DestinationsNavigator
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    var sizeState by remember {
        mutableStateOf(200.dp)
    }
    val size by animateDpAsState(
        targetValue = sizeState,
        animationSpec = spring(
            Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Box(
        modifier = Modifier.fillMaxSize().background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text("Make the RED box fill the screen!", style = MaterialTheme.typography.h5)
    }

    Box(
        modifier = Modifier
            .size(size)
            .background(Color.Red),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = { sizeState += 50.dp }) {
            Text(text = "Increase Size")
        }
    }

    //Show win animation if box fills entire screen
    if (sizeState.value >= screenHeight.value && sizeState.value >= screenWidth.value) {

        navigator.navigate(WindowsAnimationDestination())
    }

    //make the Box become smaller until it reaches original size
    if (sizeState > 200.dp) {
        sizeState -= 3.dp
    }

}

@Destination
@ExperimentalAnimationApi
@Composable
fun WindowsAnimation(
    navigator: DestinationsNavigator,
    animationText: List<String> = listOf(
        "Congratulations",
        "You Won!"
    )
) {
    var visible by remember { mutableStateOf(false) }
    val animationDuration = 1000
    val textStayTime: Long = 2000
    val fontFamily = FontFamily(Font(R.font.segoeuil, FontWeight.Normal))
    var textState by remember {
        mutableStateOf("")
    }

    val infiniteTransition = rememberInfiniteTransition()
    //background color infinite transition animation
    val color by infiniteTransition.animateColor(
        initialValue = Blue,
        targetValue = DarkBlue,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 3000),
            repeatMode = RepeatMode.Reverse
        )
    )

    //only runs once
    LaunchedEffect(key1 = true) {
        //animate the fade in->out for all Strings in animationText
        for (s in animationText) {
            visible = true
            textState = s
            delay(textStayTime)
            visible = false
            delay(textStayTime)
        }
        navigator.popBackStack()
        navigator.navigate(ClickGameDestination())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        //text fade animation
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(durationMillis = animationDuration)),
            exit = fadeOut(animationSpec = tween(durationMillis = animationDuration))
        ) {
            Text(
                text = textState,
                fontFamily = fontFamily,
                style = MaterialTheme.typography.h5,
                color = Color.White
            )
        }
    }
}

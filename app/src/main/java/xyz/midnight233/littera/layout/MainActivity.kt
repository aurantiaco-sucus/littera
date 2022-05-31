package xyz.midnight233.littera.layout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.midnight233.littera.stateful.GameplayStateType
import xyz.midnight233.littera.stateful.LitteraPage
import xyz.midnight233.littera.stateful.LitteraState
import xyz.midnight233.littera.stateful.rememberLitteraState
import xyz.midnight233.littera.theme.LitteraTheme
import xyz.midnight233.dome41.registerDome41Artifacts
import xyz.midnight233.littera.theme.Primary

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerDome41Artifacts()
        setContent {
            LitteraTheme {
                RootPage(rememberLitteraState(applicationContext))
            }
        }
    }
}

fun <T> T.oneOf(vararg values: T) = values.any { it == this }

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RootPage(litteraState: LitteraState) {
    // Entrance page animation
    val scaleFraction by animateFloatAsState(targetValue =
    if (litteraState.currentPage == LitteraPage.Entrance) 2.5f else 1f)
    val backgroundColor by animateColorAsState(targetValue =
    if (litteraState.currentPage == LitteraPage.Entrance)
        MaterialTheme.colors.surface
    else
        MaterialTheme.colors.primary)
    val textColor by animateColorAsState(targetValue =
    if (litteraState.currentPage == LitteraPage.Entrance)
        MaterialTheme.colors.onSurface
    else
        MaterialTheme.colors.onPrimary)

    // Bottom navigation bar animation
    val sheetState = litteraState.bottomSheetScaffoldState.bottomSheetState
    val sheetFraction =
        if (litteraState.currentPage == LitteraPage.Journal) {
            when {
                litteraState.interactiveState.gameplayState.oneOf(
                    GameplayStateType.Response,
                    GameplayStateType.Continue
                ) -> 1f
                sheetState.direction == 0f -> {
                    when (sheetState.currentValue) {
                        BottomSheetValue.Expanded -> 0f
                        BottomSheetValue.Collapsed -> 1f
                    }
                }
                else -> {
                    when (sheetState.progress.to) {
                        BottomSheetValue.Expanded -> 1f - sheetState.progress.fraction
                        BottomSheetValue.Collapsed -> sheetState.progress.fraction
                    }
                }
            }
        } else 1f
    val cornerRadius by animateDpAsState(targetValue = when (litteraState.currentPage) {
        LitteraPage.Journal -> when (litteraState.interactiveState.gameplayState) {
            GameplayStateType.Response, GameplayStateType.Continue -> 0.dp
            else -> 20.dp
        }
        else -> 0.dp
    })

    Surface(color = MaterialTheme.colors.background) {
        BottomSheetScaffold(
            scaffoldState = litteraState.bottomSheetScaffoldState,
            sheetContent = {
                Column(
                    Modifier.onSizeChanged {
                        litteraState.bottomSheetHeight = it.height.toFloat()
                    }
                ) {
                    RootBottomSheetBar(
                        litteraState,
                        backgroundColor,
                        textColor,
                        sheetFraction
                    )
                    if (litteraState.currentPage == LitteraPage.Journal) {
                        InteractiveView(litteraState)
                    }
                }
            },
            topBar = {
                TopAppBar(
                    modifier = Modifier.height(56.dp * scaleFraction),
                    backgroundColor = backgroundColor,
                    elevation = 0.dp,
                ) {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(
                        text = "Littera",
                        fontSize = 20.sp * scaleFraction,
                        color = textColor,
                    )
                }
            },
            sheetElevation = 0.dp,
            sheetShape = RoundedCornerShape(
                topStart = cornerRadius,
                topEnd = cornerRadius
            ),
            floatingActionButton = {
                ConfirmButton(litteraState)
            },
        ) { paddingValues ->
            SubPagesView(paddingValues, litteraState)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RowScope.RootPageNavigationItem(
    state: LitteraState,
    icon: ImageVector,
    label: String,
    target: LitteraPage
) {
    BottomNavigationItem(
        selected = state.currentPage == target,
        onClick = {
            state.currentPage = target
        },
        label = {
            Text(text = label)
        },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label
            )
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun isConfirmationAvailable(litteraState: LitteraState): Boolean {
    val interactiveState = litteraState.interactiveState
    if (litteraState.currentPage != LitteraPage.Journal) return false
    if (litteraState.bottomSheetScaffoldState.bottomSheetState.isCollapsed) return false
    if (interactiveState.gameplayState == GameplayStateType.Response) return false
    return when (interactiveState.gameplayState) {
        GameplayStateType.Action,
        GameplayStateType.Choice,
        GameplayStateType.Confirm ->
            interactiveState.response != ""
        GameplayStateType.Multi ->
            interactiveState.multiPredicate(interactiveState.response.lines())
        GameplayStateType.Input ->
            interactiveState.inputPredicate(interactiveState.response)
        else -> true
    }
}

@Composable
fun ConfirmButton(litteraState: LitteraState) {
    val interactiveState = litteraState.interactiveState
    val offset by animateDpAsState(targetValue =
    if (interactiveState.gameplayState == GameplayStateType.Continue) (-56).dp else 0.dp)
    AnimatedVisibility(
        visible = isConfirmationAvailable(litteraState),
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.offset(y = offset)
    ) {
        FloatingActionButton(onClick = {
            interactiveState.gameplayState = GameplayStateType.Response
        }) {
            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = "Confirm",
            )
        }
    }
}

@Composable
fun SubPagesView(paddingValues: PaddingValues, litteraState: LitteraState) {
    Crossfade(targetState = litteraState.currentPage) { page ->
        when (page) {
            LitteraPage.Entrance -> EntrancePage(
                litteraState = litteraState,
                paddingValues = paddingValues
            )
            LitteraPage.Journal -> JournalPage(
                litteraState = litteraState,
                paddingValues = paddingValues
            )
            LitteraPage.Notebook -> NotebookPage(
                litteraState = litteraState,
                paddingValues = paddingValues
            )
        }
    }
}

@Composable
fun RootBottomSheetBar(
    litteraState: LitteraState,
    backgroundColor: Color,
    textColor: Color,
    sheetFraction: Float,
) {
    AnimatedVisibility(
        visible = litteraState.gameplayReady,
        enter = slideInVertically { it }
    ) {
        BottomNavigation(
            backgroundColor = backgroundColor,
            contentColor = textColor,
            elevation = 0.dp,
            modifier = Modifier
                .height(56.dp * sheetFraction),
        ) {
            RootPageNavigationItem(
                state = litteraState,
                icon = Icons.Filled.Home,
                label = "Entrance",
                target = LitteraPage.Entrance
            )
            RootPageNavigationItem(
                state = litteraState,
                icon = Icons.Filled.Edit,
                label = "Journal",
                target = LitteraPage.Journal
            )
            RootPageNavigationItem(
                state = litteraState,
                icon = Icons.Filled.Star,
                label = "Notebook",
                target = LitteraPage.Notebook
            )
        }
    }
    TopAppBar(
        modifier = Modifier
            .height(56.dp * (1f - sheetFraction)),
        backgroundColor = MaterialTheme.colors.primary,
        elevation = 0.dp,
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = "Actions",
            fontSize = 20.sp,
            color = MaterialTheme.colors.onPrimary
        )
    }
}
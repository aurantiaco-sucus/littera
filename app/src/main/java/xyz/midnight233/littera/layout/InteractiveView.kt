package xyz.midnight233.littera.layout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.midnight233.littera.stateful.*

@Composable
fun InteractiveView(litteraState: LitteraState) {
    val interactiveState = litteraState.interactiveState
    val isVisible = !interactiveState.gameplayState
        .oneOf(GameplayStateType.Response, GameplayStateType.Continue)
    val isPrompting = isVisible && interactiveState.gameplayState != GameplayStateType.Action
    if (isVisible) {
        Spacer(modifier = Modifier.height(1.dp))
    }
    AnimatedVisibility(visible = isPrompting) {
        Column(
            Modifier.padding(20.dp)
        ) {
            PromptEntry(alpha = 1f, content = interactiveState.prompt)
        }
    }
    Crossfade(targetState = interactiveState.gameplayState) {
        when (it) {
            GameplayStateType.Action -> ActionView(interactiveState)
            GameplayStateType.Choice -> ChoiceView(interactiveState)
            GameplayStateType.Multi -> MultiChoiceView(interactiveState)
            GameplayStateType.Input -> InputView(interactiveState)
            GameplayStateType.Confirm -> ConfirmView(interactiveState)
            else -> {}
        }
    }
}

@Composable
fun ActionView(interactiveState: InteractiveState) {
    var selectedTab by remember { mutableStateOf(ActionEntryType.Context) }
    Column {
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface,
        ) {
            enumValues<ActionEntryType>().forEach { category ->
                Tab(
                    selected = category == selectedTab,
                    onClick = {
                        selectedTab = category
                    }
                ) {
                    Text(
                        text = category.name,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        LazyColumn(
            Modifier.padding(horizontal = 20.dp),
        ) {
            items(interactiveState.actionCandidates) { entry ->
                if (selectedTab == entry.type) {
                    ActionCard(interactiveState, entry)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ActionCard(interactiveState: InteractiveState, entry: ActionEntry) {
    val index = interactiveState.actionCandidates.indexOf(entry).toString()
    val selected = interactiveState.response == index
    val borderColor by animateColorAsState(targetValue = if (selected) MaterialTheme.colors.primary
    else MaterialTheme.colors.surface)
    val backColor by animateColorAsState(targetValue =
        if (selected) MaterialTheme.colors.primaryVariant
        else MaterialTheme.colors.surface)
    Card(
        onClick = {
            interactiveState.response = index
        },
        shape = RoundedCornerShape(15.dp),
        backgroundColor = backColor,
        contentColor = contentColorFor(backColor),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier.padding(15.dp),
        ) {
            Text(
                text = entry.name,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = entry.description,
            )
        }
    }
}

@Composable
fun ChoiceView(interactiveState: InteractiveState) {
    val candidates = interactiveState.candidates
    LazyColumn(
        Modifier.padding(horizontal = 20.dp),
    ) {
        items(candidates) { entry ->
            Column {
                ChoiceCard(entry, interactiveState.response == entry) {
                    interactiveState.response = entry
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChoiceCard(name: String, selected: Boolean, onClick: () -> Unit) {
    val borderColor by animateColorAsState(targetValue =
    if (selected) MaterialTheme.colors.secondary
    else MaterialTheme.colors.onSurface.copy(alpha = 0.33f))
    val backColor by animateColorAsState(targetValue =
    if (selected) MaterialTheme.colors.secondary
    else MaterialTheme.colors.surface)
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        backgroundColor = backColor,
        contentColor = contentColorFor(backColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            Modifier
                .padding(vertical = 10.dp, horizontal = 20.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = name,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
fun MultiChoiceView(interactiveState: InteractiveState) {
    val candidates = interactiveState.candidates
    LazyColumn(
        Modifier.padding(horizontal = 20.dp),
    ) {
        items(candidates) { entry ->
            val selected = interactiveState.response.lines().contains(entry)
            Column {
                ChoiceCard(entry, selected) {
                    if (selected) {
                        interactiveState.response =
                            interactiveState.response.replace(entry, "")
                    } else {
                        interactiveState.response =
                            interactiveState.response + entry
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun InputView(interactiveState: InteractiveState) {
    Column(
        Modifier.imePadding()
    ) {
        TextField(
            value = interactiveState.response,
            onValueChange = {
                interactiveState.response = it
            },
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            label = {
                Text(text = "Your answer")
            }
        )
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun ConfirmView(interactiveState: InteractiveState) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        IconToggleButton(
            checked = interactiveState.response == "true",
            onCheckedChange = {
                interactiveState.response = "true"
            }
        ) {
            Icon(imageVector = Icons.Default.Done, contentDescription = "Yes")
        }
        IconToggleButton(
            checked = interactiveState.response == "false",
            onCheckedChange = {
                interactiveState.response = "false"
            }
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "No")
        }
    }
}

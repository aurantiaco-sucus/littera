package xyz.midnight233.littera.layout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import xyz.midnight233.littera.persist.Profile
import xyz.midnight233.littera.runtime.Runtime
import xyz.midnight233.littera.stateful.LitteraState
import xyz.midnight233.littera.content.Artifact
import kotlin.concurrent.thread

enum class EntrancePageViewType {
    ArtifactSelection, ProfileSelection, ProfileCreation, ProfileOverview
    // TODO: Profile Manipulation
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EntrancePage(litteraState: LitteraState, paddingValues: PaddingValues) {
    Column(Modifier.padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(paddingValues.calculateTopPadding()))
        val currentViewState = remember { mutableStateOf(EntrancePageViewType.ArtifactSelection) }
        val currentView by currentViewState
        Text(
            text = " " + when (currentView) {
                EntrancePageViewType.ArtifactSelection -> "artifact selection"
                EntrancePageViewType.ProfileSelection -> "profile selection"
                EntrancePageViewType.ProfileCreation -> "profile creation"
                EntrancePageViewType.ProfileOverview -> "profile overview"
            }.uppercase(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Crossfade(targetState = currentView) {
            when (it) {
                EntrancePageViewType.ArtifactSelection ->
                    ArtifactSelectionView(litteraState, currentViewState)
                EntrancePageViewType.ProfileSelection ->
                    ProfileSelectionView(litteraState, currentViewState)
                EntrancePageViewType.ProfileCreation ->
                    ProfileCreationView(litteraState, currentViewState)
                EntrancePageViewType.ProfileOverview ->
                    ProfileOverviewView(litteraState, currentViewState)
            }
        }
    }
}

enum class EntranceCardAction {
    Play, Settings,
}

enum class ProfileCardAction {
    Play, Rename, Delete,
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun ProfileCard(title: String, onAction: (ProfileCardAction) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(targetValue =
    if (!expanded) MaterialTheme.colors.onSurface.copy(alpha = 0.25f)
    else MaterialTheme.colors.primary)
    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, borderColor),
        elevation = 0.dp
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 15.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = title, fontSize = 25.sp)
                }
                AnimatedVisibility(visible = expanded) {
                    Column {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(Modifier.fillMaxWidth()) {
                            IconButton(
                                onClick = { onAction(ProfileCardAction.Play) },
                                modifier = Modifier.align(Alignment.CenterStart)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play"
                                )
                            }
                            Row(Modifier.align(Alignment.CenterEnd)) {
                                IconButton(
                                    onClick = { onAction(ProfileCardAction.Rename) },
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Rename"
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                IconButton(
                                    onClick = { onAction(ProfileCardAction.Delete) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun ArtifactCard(title: String, author: String, onAction: (EntranceCardAction) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(targetValue =
    if (expanded) MaterialTheme.colors.primary
    else MaterialTheme.colors.onSurface.copy(alpha = 0.25f))
    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, borderColor),
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 15.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = title, fontSize = 25.sp)
                    AnimatedVisibility(visible = expanded) {
                        Row {
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = author, fontSize = 15.sp)
                        }
                    }
                }
                AnimatedVisibility(visible = expanded) {
                    Column {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(Modifier.fillMaxWidth()) {
                            IconButton(
                                onClick = { onAction(EntranceCardAction.Play) },
                                modifier = Modifier.align(Alignment.CenterStart)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play"
                                )
                            }
                            IconButton(
                                onClick = { onAction(EntranceCardAction.Settings) },
                                modifier = Modifier.align(Alignment.CenterEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArtifactSelectionView(
    litteraState: LitteraState,
    currentViewState: MutableState<EntrancePageViewType>
) {
    var currentView by currentViewState
    //val coroutineScope = rememberCoroutineScope()
    LazyColumn {
        items(Artifact.globalInstances) { instance ->
            ArtifactCard(
                title = instance.title,
                author = instance.author
            ) {
                when (it) {
                    EntranceCardAction.Play -> {
                        Runtime.artifact = instance
                        thread {
                            val profiles = litteraState.litteraBase
                                .profileDao()
                                .forArtifact(instance)
                            currentView = if (profiles.isEmpty()) {
                                EntrancePageViewType.ProfileCreation
                            } else {
                                EntrancePageViewType.ProfileSelection
                            }
                        }
                    }
                    EntranceCardAction.Settings -> {
                        // TODO: Profile management...
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable fun ProfileSelectionView(
    litteraState: LitteraState,
    currentViewState: MutableState<EntrancePageViewType>
) {
    var profiles by remember { mutableStateOf(listOf<Profile>()) }
    SideEffect {
        thread {
            profiles = litteraState.litteraBase.profileDao().forArtifact(Runtime.artifact)
        }
    }
    LazyColumn {
        items(profiles) { profile ->
            ProfileCard(
                title = profile.name,
            ) {
                when (it) {
                    ProfileCardAction.Play -> {
                        Profile.instance = profile
                        litteraState.journalALCState.items += Profile.pullJournal()
                        litteraState.journalALCState.coroutineScope.launch {
                            delay(250)
                            litteraState.journalALCState.lazyColumnState.scrollToItem(
                                litteraState.journalALCState.items.size - 1
                            )
                        }
                        Runtime.ignite()
                        litteraState.gameplayReady = true
                        currentViewState.value = EntrancePageViewType.ProfileOverview
                    }
                    ProfileCardAction.Rename -> TODO()
                    ProfileCardAction.Delete -> TODO()
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

fun String.containsAny(vararg strings: String) = strings.any { this.contains(it) }

val String.acceptableProfileName get() = !this.containsAny(
    ",", ".", ":", "'", "\"", "/", "\\", "<", ">", "?", "!", "#", "$", "%", "&", "*", "(", ")",
    "+", "="
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ProfileCreationView(
    litteraState: LitteraState,
    currentViewState: MutableState<EntrancePageViewType>
) {
    var currentView by currentViewState
    Column {
        Text(
            text = "create a profile",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(20.dp))
        var profileName by remember { mutableStateOf("") }
        TextField(
            value = profileName,
            onValueChange = {
                profileName = it
            },
            label = {
                Text(text = "Name")
            },
            isError = !profileName.acceptableProfileName,
        )
        Spacer(modifier = Modifier.height(20.dp))
        AnimatedVisibility(visible = !profileName.acceptableProfileName) {
            Column {
                Text(
                    text = "Profile name should not contain special symbols.",
                    color = MaterialTheme.colors.error
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
        Button(
            onClick = {
                if (profileName.acceptableProfileName) {
                    thread {
                        Profile.instance = Runtime.artifact.createProfile(profileName)
                        Runtime.artifact.template.entries.forEach {
                            Profile[it.key] = it.value
                        }
                        litteraState.litteraBase.profileDao()
                            .insert(Profile.instance)
                        Runtime.ignite()
                        litteraState.gameplayReady = true
                        currentView = EntrancePageViewType.ProfileOverview
                    }
                }
            }
        ) {
            Text(text = "Create")
            Spacer(modifier = Modifier.width(10.dp))
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Create")
        }
    }
}

@Composable
fun ProfileOverviewView(
    litteraState: LitteraState,
    currentViewState: MutableState<EntrancePageViewType>
) {
    var currentView by currentViewState
    val currentProfile by remember { mutableStateOf(Profile.instance) }
    LazyColumn {
        item {
            ProfileOverviewEntry(name = "name", content = currentProfile.name)
        }
    }
}

@Composable
fun ProfileOverviewEntry(name: String, content: String) {
    Text(
        text = name.uppercase(),
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
    )
    Spacer(modifier = Modifier.height(5.dp))
    Text(
        text = content,
    )
    Spacer(modifier = Modifier.height(20.dp))
}
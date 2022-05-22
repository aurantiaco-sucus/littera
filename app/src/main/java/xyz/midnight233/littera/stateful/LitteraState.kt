package xyz.midnight233.littera.stateful

import android.content.Context
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.room.Room
import xyz.midnight233.littera.layout.AnimatedLazyColumnState
import xyz.midnight233.littera.layout.rememberAnimatedLazyColumnState
import xyz.midnight233.littera.persist.LitteraBase

@OptIn(ExperimentalMaterialApi::class)
class LitteraState(
    val bottomSheetScaffoldState: BottomSheetScaffoldState,
    currentPageState: MutableState<LitteraPage>,
    val journalALCState: AnimatedLazyColumnState<JournalEntry>,
    val notebookEntries: SnapshotStateList<NotebookEntry>,
    val litteraBase: LitteraBase,
    gameplayReadyState: MutableState<Boolean>,
    val interactiveState: InteractiveState,
    bottomSheetHeightState: MutableState<Float>,
) {
    var currentPage by currentPageState
    var gameplayReady by gameplayReadyState
    var bottomSheetHeight by bottomSheetHeightState

    init {
        instance = this
    }

    companion object {
        lateinit var instance: LitteraState
    }
}

enum class LitteraPage {
    Entrance, Journal, Notebook
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberLitteraState(applicationContext: Context) = LitteraState(
    bottomSheetScaffoldState = rememberBottomSheetScaffoldState(),
    currentPageState = remember { mutableStateOf(LitteraPage.Entrance) },
    journalALCState = rememberAnimatedLazyColumnState(
        defaultTemporaryItem = JournalEntry.default),
    notebookEntries = remember { mutableStateListOf() },
    litteraBase = Room.databaseBuilder(
        applicationContext,
        LitteraBase::class.java,
        "littera").build(),
    gameplayReadyState = remember { mutableStateOf(false) },
    interactiveState = rememberInteractiveState(),
    bottomSheetHeightState = remember { mutableStateOf(0f) }
)

enum class GameplayStateType {
    Response, Action, Choice, Multi, Input, Confirm, Continue
}

class InteractiveState(
    gameplayStateObj: MutableState<GameplayStateType>,
    responseState: MutableState<String>,
    val candidates: SnapshotStateList<String>,
    multiPredicateState: MutableState<(List<String>) -> Boolean>,
    inputPredicateState: MutableState<(String) -> Boolean>,
    val actionCandidates: SnapshotStateList<ActionEntry>,
    promptState: MutableState<String>,
) {
    var gameplayState by gameplayStateObj
    var response by responseState
    var multiPredicate by multiPredicateState
    var inputPredicate by inputPredicateState
    var prompt by promptState
}

@Composable
fun rememberInteractiveState() = InteractiveState(
    gameplayStateObj = remember { mutableStateOf(GameplayStateType.Response) },
    responseState = remember { mutableStateOf("") },
    candidates = remember { mutableStateListOf() },
    multiPredicateState = remember { mutableStateOf({ true }) },
    inputPredicateState = remember { mutableStateOf({ true }) },
    actionCandidates = remember { mutableStateListOf() },
    promptState = remember { mutableStateOf("") }
)
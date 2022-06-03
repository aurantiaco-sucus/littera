package xyz.midnight233.littera.layout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AnimatedLazyColumnState<T>(
    val temporaryItem: MutableState<T>,
    val temporaryItemExistence: MutableState<Boolean>,
    val temporaryItemVisibilityState: Animatable<Float, AnimationVector1D>,
    val items: SnapshotStateList<T>,
    val coroutineScope: CoroutineScope,
    val defaultTemporaryItem: T,
    val lazyColumnState: LazyListState
) {
    fun append(item: T) {
        while (temporaryItemExistence.value) {
            Thread.sleep(250)
        }
        temporaryItem.value = item
        temporaryItemExistence.value = true
        coroutineScope.launch {
            temporaryItemVisibilityState.animateTo(1f)
        }
    }
}

@Composable
fun <T> rememberAnimatedLazyColumnState(defaultTemporaryItem: T)
= AnimatedLazyColumnState(
    temporaryItem = remember { mutableStateOf(defaultTemporaryItem) },
    temporaryItemExistence = remember { mutableStateOf(false) },
    temporaryItemVisibilityState = remember { Animatable(0f) },
    items = remember { mutableStateListOf() },
    coroutineScope = rememberCoroutineScope(),
    defaultTemporaryItem = defaultTemporaryItem,
    lazyColumnState = rememberLazyListState()
)

@Composable
fun <T> AnimatedLazyColumn(
    state: AnimatedLazyColumnState<T>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    callback: (List<T>) -> Unit = {},
    template: @Composable (item: T, fraction: Float) -> Unit,
) {
    var temporaryItem by state.temporaryItem
    var temporaryItemExistence by state.temporaryItemExistence
    val temporaryItemVisibility by state.temporaryItemVisibilityState.asState()
    LazyColumn(
        modifier = modifier,
        state = state.lazyColumnState,
        contentPadding = contentPadding,
    ) {
        items(state.items) { template(it, 1f) }
        if (temporaryItemExistence) item {
            template(temporaryItem, temporaryItemVisibility)
            LaunchedEffect(state.temporaryItemVisibilityState.isRunning) {
                if (state.temporaryItemVisibilityState.isRunning) return@LaunchedEffect
                state.items += temporaryItem
                temporaryItem = state.defaultTemporaryItem
                state.temporaryItemVisibilityState.snapTo(0f)
                callback(state.items)
                temporaryItemExistence = false
            }
        }
    }
    LaunchedEffect(temporaryItemExistence) {
        if (temporaryItemExistence) return@LaunchedEffect
        if (state.items.size == 0) return@LaunchedEffect
        state.lazyColumnState.scrollToItem(state.items.size - 1)
    }
}
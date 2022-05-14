package xyz.midnight233.littera.layout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import xyz.midnight233.littera.stateful.LitteraState
import xyz.midnight233.littera.stateful.NotebookEntry
import xyz.midnight233.littera.stateful.NotebookEntryType

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotebookPage(litteraState: LitteraState, paddingValues: PaddingValues) {
    var categorySelection by remember { mutableStateOf(NotebookEntryType.Todo) }
    val entries = litteraState.notebookEntries
    Column(
        Modifier.padding(
            top = paddingValues.calculateTopPadding(),
            bottom = paddingValues.calculateBottomPadding()
        )
    ) {
//        Button(onClick = {
//            entries += NotebookEntry(
//                title = "Test #${entries.size + 1}",
//                type = NotebookEntryType.Plot,
//                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
//                        "Sed euismod, urna eu tempor consectetur, nisi nisl " +
//                        "elementum nunc, eget euismod nisl nunc euismod nisl. "
//            )
//        }) {
//            Text(text = "Add!")
//        }
        TabRow(
            selectedTabIndex = categorySelection.ordinal,
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface,
        ) {
            enumValues<NotebookEntryType>().forEach { category ->
                Tab(
                    selected = category == categorySelection,
                    onClick = {
                        categorySelection = category
                    }
                ) {
                    Text(
                        text = category.name,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
            }
        }
        LazyColumn {
            items(entries) { entry ->
                if (categorySelection == entry.type) {
                    NotebookEntry(entry)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun NotebookPage2(litteraState: LitteraState, paddingValues: PaddingValues) {
    val entries = litteraState.notebookEntries
    val pagerState = rememberPagerState()
    val coroutineState = rememberCoroutineScope()
    Column {
        Button(onClick = {
            entries += NotebookEntry(
                title = "Test #${entries.size + 1}",
                type = NotebookEntryType.Plot,
                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                        "Sed euismod, urna eu tempor consectetur, nisi nisl " +
                        "elementum nunc, eget euismod nisl nunc euismod nisl. "
            )
        }) {
            Text(text = "Add!")
        }
        TabRow(
            // Our selected tab is our current page
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                )
            }
        ) {
            NotebookEntryType.values().forEach { category ->
                Tab(
                    selected = pagerState.currentPage == category.ordinal,
                    onClick = {
                        coroutineState.launch {
                            pagerState.animateScrollToPage(category.ordinal)
                        }
                    }
                ) {
                    Text(
                        text = category.name,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
            }
        }
        HorizontalPager(
            count = NotebookEntryType.values().size,
            state = pagerState,
        ) { page ->
            LazyColumn {
                items(entries) { entry ->
                    if (page == entry.type.ordinal) {
                        NotebookEntry(entry)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun NotebookEntry(entry: NotebookEntry) {
    var expanded by remember { mutableStateOf(false) }
    val elevation by animateDpAsState(targetValue = if (expanded) 0.dp else 5.dp)
    val borderColor by animateColorAsState(targetValue =
        if (expanded) MaterialTheme.colors.secondary else Color.Transparent)
    val cornerRadius by animateDpAsState(targetValue = if (expanded) 10.dp else 15.dp)
    val fontSize by animateFloatAsState(targetValue = if (expanded) 15f else 25f)
    val fontWeight by animateIntAsState(targetValue =
        if (expanded) FontWeight.Bold.weight else FontWeight.Normal.weight)
    val padding by animateDpAsState(targetValue = if (expanded) 10.dp else 10.dp)
    Card(
        onClick = {
            expanded = !expanded
        },
        shape = RoundedCornerShape(cornerRadius),
        elevation = elevation,
        border = BorderStroke(width = 2.dp, color = borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Column {
            Text(
                text = entry.title,
                fontSize = fontSize.sp,
                fontWeight = FontWeight(fontWeight),
                modifier = Modifier.padding(padding)
            )
            AnimatedVisibility(visible = expanded) {
                Column(
                    Modifier.padding(horizontal = 10.dp)
                ) {
                    Text(
                        text = entry.content,
                        fontSize = 25.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}
package xyz.midnight233.littera.piece

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.midnight233.littera.stateful.LitteraState

@Composable
fun LayoutTemplate(litteraState: LitteraState, paddingValues: PaddingValues) {
    // Skeleton
}

@Composable
fun PieceTemplate(modifier: Modifier = Modifier) {
    // Skeleton
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScaffoldCardFiller(it: PaddingValues) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp)
    ) {
        items(30) {
            Card(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                Text(text = "Card, Number $it")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Spacer(modifier = Modifier.height(it.calculateBottomPadding() - 16.dp))
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SimpleCardFiller() {
    LazyColumn(
        contentPadding = PaddingValues(16.dp)
    ) {
        items(30) {
            Card(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                Text(text = "Card, Number $it")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
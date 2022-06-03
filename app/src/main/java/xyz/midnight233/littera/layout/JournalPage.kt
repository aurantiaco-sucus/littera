package xyz.midnight233.littera.layout

import android.content.res.Resources
import android.util.TypedValue
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import xyz.midnight233.littera.persist.Profile
import xyz.midnight233.littera.stateful.JournalEntry
import xyz.midnight233.littera.stateful.JournalEntryType
import xyz.midnight233.littera.stateful.LitteraState
import kotlin.concurrent.thread

val Number.fromPx get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_PX,
    this.toFloat(),
    Resources.getSystem().displayMetrics
)

@Composable
fun JournalPage(litteraState: LitteraState, paddingValues: PaddingValues) {
    Column(
        Modifier.padding(
            start = 15.dp,
            end = 15.dp,
            top = paddingValues.calculateTopPadding(),
            bottom = paddingValues.calculateBottomPadding()
        )
    ) {
        AnimatedLazyColumn(
            state = litteraState.journalALCState,
            contentPadding = PaddingValues(top = 20.dp),
            callback = {
                thread {
                    Profile.pushJournal(it)
                }
            }
        ) { item, alpha ->
            when (item.type) {
                JournalEntryType.Narration ->
                    NarrationEntry(alpha = alpha, content = item.contents[0])
                JournalEntryType.Dialogue ->
                    DialogueEntry(
                        alpha = alpha,
                        subject = item.contents[0],
                        content = item.contents[1])
                JournalEntryType.Notification ->
                    NotificationEntry(alpha = alpha, content = item.contents[0])
                JournalEntryType.Prompt ->
                    PromptEntry(alpha = alpha, content = item.contents[0])
                JournalEntryType.Answer ->
                    AnswerEntry(alpha = alpha, content = item.contents[0])
            }
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}

@Composable
fun NarrationEntry(alpha: Float, content: String) {
    Text(
        text = content,
        fontFamily = FontFamily.Serif,
        fontSize = 25.sp,
        modifier = Modifier
            .scale(alpha)
            .alpha(alpha)
    )
}

@Composable
fun DialogueEntry(alpha: Float, subject: String, content: String) {
    Row {
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = subject,
            fontFamily = FontFamily.Serif,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .scale(alpha)
                .alpha(alpha)
        )
    }
    Text(
        text = content,
        fontFamily = FontFamily.Serif,
        fontSize = 25.sp,
        modifier = Modifier
            .scale(alpha)
            .alpha(alpha)
    )
}

@Composable
fun NotificationEntry(alpha: Float, content: String) {
    Text(
        text = content,
        fontFamily = FontFamily.Monospace,
        fontSize = 20.sp,
        color = MaterialTheme.colors.secondary,
        modifier = Modifier
            .alpha(alpha)
    )
}

@Composable
fun PromptEntry(alpha: Float, content: String) {
    Card(
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp, bottomEnd = 30.dp),
        elevation = 0.dp,
        border = BorderStroke(2.dp, MaterialTheme.colors.secondary),
        modifier = Modifier
            .scale(alpha)
            .alpha(alpha)
    ) {
        Text(
            text = content,
            fontFamily = FontFamily.SansSerif,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(15.dp)
        )
    }
}

@Composable
fun AnswerEntry(alpha: Float, content: String) {
    Card(
        shape = RoundedCornerShape(topEnd = 30.dp, bottomStart = 30.dp, bottomEnd = 30.dp),
        elevation = 0.dp,
        border = BorderStroke(2.dp, MaterialTheme.colors.secondary),
        modifier = Modifier
            .scale(alpha)
            .alpha(alpha)
    ) {
        Text(
            text = content,
            fontFamily = FontFamily.SansSerif,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(15.dp)
        )
    }
}
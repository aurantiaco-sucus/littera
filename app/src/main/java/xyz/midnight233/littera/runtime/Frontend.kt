package xyz.midnight233.littera.runtime

import android.util.Log
import kotlinx.coroutines.delay
import xyz.midnight233.littera.stateful.*
import kotlin.concurrent.thread

object Frontend {
    val interactiveState get() = LitteraState.instance.interactiveState
    val litteraState get() = LitteraState.instance

    private fun waitForResponse() {
        while (interactiveState.gameplayState != GameplayStateType.Response) {
            Thread.sleep(250)
        }
    }

    private fun clearResponse() {
        interactiveState.response = ""
    }

    fun appendJournal(type: JournalEntryType, vararg contents: String) {
        //litteraState.journalALCState.items += JournalEntry(type, contents.toList())
        litteraState.journalALCState.append(JournalEntry(type, contents.toList()))
        if (type == JournalEntryType.Prompt) interactiveState.prompt = contents[0]
    }

    fun requestChoice(prompt: String, choices: List<String>): String {
        clearResponse()
        appendJournal(JournalEntryType.Prompt, prompt)
        interactiveState.candidates.clear()
        interactiveState.candidates.addAll(choices)
        interactiveState.gameplayState = GameplayStateType.Choice
        waitForResponse()
        Log.e("LitteraRT", "ANSWER SENT")
        appendJournal(JournalEntryType.Answer, interactiveState.response)
        return interactiveState.response
    }

    fun requestInput(prompt: String, predicate: (String) -> Boolean = { true }): String {
        clearResponse()
        appendJournal(JournalEntryType.Prompt, prompt)
        interactiveState.inputPredicate = predicate
        interactiveState.gameplayState = GameplayStateType.Input
        waitForResponse()
        appendJournal(JournalEntryType.Answer, interactiveState.response)
        return interactiveState.response
    }

    fun requestMultipleChoices(
        prompt: String,
        choices: List<String>,
        predicate: (List<String>) -> Boolean = { true }
    ): List<String> {
        clearResponse()
        appendJournal(JournalEntryType.Prompt, prompt)
        interactiveState.multiPredicate = predicate
        interactiveState.gameplayState = GameplayStateType.Multi
        waitForResponse()
        appendJournal(JournalEntryType.Answer, interactiveState.response)
        return interactiveState.response.lines()
    }

    fun requestConfirmation(prompt: String): Boolean {
        clearResponse()
        appendJournal(JournalEntryType.Prompt, prompt)
        interactiveState.gameplayState = GameplayStateType.Confirm
        waitForResponse()
        appendJournal(JournalEntryType.Answer, interactiveState.response)
        return interactiveState.response == "true"
    }

    fun requestAction(actions: List<ActionEntry>): ActionEntry {
        clearResponse()
        interactiveState.actionCandidates.clear()
        interactiveState.actionCandidates += actions
        interactiveState.gameplayState = GameplayStateType.Action
        waitForResponse()
        return actions[interactiveState.response.toInt()]
    }

    fun requestContinuation() {
        clearResponse()
        interactiveState.gameplayState = GameplayStateType.Continue
        waitForResponse()
    }
}
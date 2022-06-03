package xyz.midnight233.littera.content

import xyz.midnight233.littera.runtime.Frontend
import xyz.midnight233.littera.runtime.Runtime
import xyz.midnight233.littera.stateful.JournalEntryType

object ContentScope : PredicateScope {
    fun String.speak(vararg contents: String) {
        Frontend.appendJournal(
            JournalEntryType.Dialogue,
            this,
            contents.joinToString(" "))
        Frontend.requestContinuation()
    }

    fun narrate(vararg contents: String) {
        Frontend.appendJournal(JournalEntryType.Narration, contents.joinToString(" "))
        Frontend.requestContinuation()
    }

    fun notify(content: String) = Frontend
        .appendJournal(JournalEntryType.Notification, content)

    fun input(prompt: String, predicate: (String) -> Boolean = { true }): String = Frontend
        .requestInput(prompt, predicate)

    class ChoiceScope {
        val choices = mutableListOf<Pair<String, ContentScope.() -> Unit>>()

        fun bind(choice: String, callback: ContentScope.() -> Unit) {
            choices += choice to callback
        }
    }

    fun choose(prompt: String, builder: ChoiceScope.() -> Unit) {
        val choices = ChoiceScope().also(builder).choices
        choices
            .find { it.first == Frontend.requestChoice(prompt, choices
                .map { it.first }) }!!
            .second(ContentScope)
    }

    fun navigate(target: Segment) {
        Runtime.currentScene = target
    }
}
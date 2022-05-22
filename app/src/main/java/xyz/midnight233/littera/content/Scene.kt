package xyz.midnight233.littera.content

import xyz.midnight233.littera.persist.Profile
import xyz.midnight233.littera.stateful.ActionEntry
import xyz.midnight233.littera.stateful.ActionEntryType
import xyz.midnight233.littera.stateful.NotebookEntry
import xyz.midnight233.littera.stateful.NotebookEntryType

abstract class Scene {
    val memo get() = Profile
        .scene(this::class.qualifiedName!!)
        .memoDelegate
    val mark get() = Profile
        .scene(this::class.qualifiedName!!)
        .markDelegate

    val notes = mutableListOf<NotebookEntry>()
    fun note(
        title: String,
        type: NotebookEntryType,
        content: String
    ): Profile.Companion.NavNamedDelegate<Boolean> {
        val entry = NotebookEntry(title, type, content)
        notes += entry
        return Profile
            .scene(this::class.qualifiedName!!)
            .mark("Note#${notes.indexOf(entry)}")
    }

    val events = mutableListOf<Pair<PredicateScope.() -> Boolean, ContentScope.() -> Unit>>()
    fun event(
        condition: PredicateScope.() -> Boolean = { true },
        content: ContentScope.() -> Unit
    ): ContentScope.() -> Unit {
        events += condition to content
        return content
    }

    val actions = mutableListOf<Triple<ActionEntry, PredicateScope.() -> Boolean, ContentScope.() -> Unit>>()
    fun action(
        name: String,
        type: ActionEntryType = ActionEntryType.Context,
        description: String = "",
        condition: PredicateScope.() -> Boolean = { true },
        content: ContentScope.() -> Unit
    ): ContentScope.() -> Unit {
        actions += Triple(ActionEntry(name, type, description), condition, content)
        return content
    }

    val plot get() = Plot(this)
}
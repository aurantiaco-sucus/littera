package xyz.midnight233.littera.runtime

import xyz.midnight233.littera.content.ContentScope
import xyz.midnight233.littera.persist.Profile
import xyz.midnight233.littera.stateful.LitteraState
import xyz.midnight233.littera.content.Artifact
import xyz.midnight233.littera.content.Scene
import kotlin.concurrent.thread

object Runtime {
    val state get() = LitteraState.instance
    lateinit var artifact: Artifact

    var currentScene
    get() = artifact.scenes.find { it::class.qualifiedName!! == Profile.currentSceneValue }!!
    set(value) { Profile.currentSceneValue = value::class.qualifiedName!! }

    fun ignite() = thread(isDaemon = true) {
        artifact.scenes.forEach(Scene::initializeNoteState)
        while (true) {
            var isActivated = false
            currentScene.events.forEach { (condition, content) ->
                if (condition(ContentScope)) {
                    isActivated = true
                    content(ContentScope)
                }
            }
            if (isActivated) {
                Profile.pushProfile()
                currentScene.updateNoteState()
                currentScene.processDelta(
                    add = { state.notebookEntries += it },
                    remove = { state.notebookEntries -= it },
                )
                continue
            }
            val actionBundles = currentScene.actions
                .filter { (_, condition, _) -> condition(ContentScope) }
            val actions = actionBundles
                .map { (action, _, _) -> action }
            val actionContent = Frontend.requestAction(actions)
                .let { actions.indexOf(it) }
                .let { actionBundles[it] }
                .let { (_, _, content) -> content }
            actionContent(ContentScope)
            currentScene.updateNoteState()
            currentScene.processDelta(
                add = { state.notebookEntries += it },
                remove = { state.notebookEntries -= it },
            )
            Profile.pushProfile()
        }
    }
}
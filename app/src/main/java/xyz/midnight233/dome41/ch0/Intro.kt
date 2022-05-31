package xyz.midnight233.dome41.ch0

import xyz.midnight233.littera.stateful.ActionEntryType
import xyz.midnight233.littera.content.Scene
import xyz.midnight233.littera.stateful.NotebookEntryType

object Intro : Scene() {
    val pIntroEngine by plot
    val pIntroArtifact by plot

    init {
        pIntroEngine before pIntroArtifact
    }

    var nIntro by note(
        title = "Intro",
        type = NotebookEntryType.Memo,
        content = "About!"
    )

    val aIntroEngine = action(
        name = "Introduction, Pt I",
        type = ActionEntryType.Context,
        description = "Something about Littera framework.",
        condition = pIntroEngine.requireActive
    ) {
        narrate(
            "Intro."
        )
        nIntro = true
        pIntroEngine.finish()
    }

    val aIntroArtifact = action(
        name = "Introduction, Pt II",
        type = ActionEntryType.Context,
        description = "Something about this fiction, Dome 41.",
        condition = pIntroArtifact.requireActive
    ) {
        narrate("Intro!")
        pIntroArtifact.finish()
    }
}
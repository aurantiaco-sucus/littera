package xyz.midnight233.dome41.ch0

import xyz.midnight233.littera.stateful.ActionEntryType
import xyz.midnight233.littera.content.Scene

object Intro : Scene() {
    val pIntroEngine by plot
    val pIntroArtifact by plot

    init {
        pIntroEngine before pIntroArtifact
    }

    val aIntroEngine = action(
        name = "Introduction, Pt I",
        type = ActionEntryType.Context,
        description = "Something about Littera framework.",
        condition = pIntroEngine.requireActive
    ) {
        narrate(
            "Intro."
        )
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
package xyz.midnight233.dome41.ch0

import xyz.midnight233.littera.stateful.ActionEntryType
import xyz.midnight233.littera.content.Segment

object Introduction : Segment() {
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
            "This piece of interactive fiction, or (sort of) a text adventure game, or",
            "(somehow) a supercharged choose-your-own-adventure game, is written in Littera, a",
            "framework for writing interactive fiction games in a Kotlin DSL."
        )
        narrate(
            "It allows authors to write decision-driven stories in a declarative way, and",
            "thus players can easily make many choices right at their fingertips."
        )
        narrate(
            "That's it! And as a player you don't have to worry about the details of the",
            "implementation of the interactive fiction itself."
        )
        "Midnight233".speak(
            "Thanks for your attention, I hope you enjoy this piece of interactive fiction!"
        )
        pIntroEngine.finish()
    }

    val aIntroArtifact = action(
        name = "Introduction, Pt II",
        type = ActionEntryType.Context,
        description = "Something about this fiction, Dome 41.",
        condition = pIntroArtifact.requireActive
    ) {
        narrate(
            "This work is a part of my long-term project, which (currently) named as \"Project",
            "Moonlight\". I started this hobby project a decade ago as somewhere I can express",
            "my feelings toward the changing world and the society around all of us."
        )
        narrate(
            "As you can expect this project is filled with scraps of individual pieces of text",
            "without continuity which is a must to form a complete work. In order to further",
            "explore this territory, I decided to make a complete story."
        )
        narrate(
            "...Originally I planned to somewhat explain the story here. But I now decided not",
            "to mess anything up right here! :)"
        )
        pIntroArtifact.finish()
        navigate(OrdinaryDayAtWork)
    }
}
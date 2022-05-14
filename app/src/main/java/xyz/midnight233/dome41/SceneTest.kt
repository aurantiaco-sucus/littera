package xyz.midnight233.dome41

import xyz.midnight233.littera.stateful.ActionEntryType
import xyz.midnight233.littera.stateful.NotebookEntryType
import xyz.midnight233.littera.struct.Scene

object SceneTest : Scene() {
    var mark1 by mark
    var mark2 by mark

    val event1 = event(condition = { !mark1 }) {
        narrate("One time, baby!")
        mark1 = true
    }

    val action1 = action(
        name = "One time action!",
        type = ActionEntryType.Context,
        description = "One time only!",
        condition = { !mark2 }
    ) {
        narrate("One time!")
        mark2 = true
    }
}
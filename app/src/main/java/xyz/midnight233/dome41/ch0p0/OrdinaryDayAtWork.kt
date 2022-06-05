package xyz.midnight233.dome41.ch0p0

import xyz.midnight233.littera.content.Segment
import xyz.midnight233.littera.stateful.ActionEntryType

object OrdinaryDayAtWork : Segment() {
    val pPrelude by plot
    val pFindTaskList by plot
    val pBrowseDocument by plot

    init {
        pFindTaskList after pPrelude
    }

    val ePrelude = event(pPrelude.requireActive) {
        narrate(
            "A man, Jack Hawkborn, works as a librarian-scholar in Sinward, the capital city of",
            "The South Kingdom. Every day, his work is to periodically sort out the books and",
            "solve the investigations task assigned by the King according to the findings of his",
            "\"Exploration force\". As there are several other staffs dedicated to the chores,",
            "he actually has more then abundant free time to look through the books for clues."
        )
        narrate(
            "Jack just woke up from his bed, which he just bought a week ago to replace the old",
            "one. Made with the latest fiber manufacturing technologies, the bed is as soft as",
            "marshmallows with the weight of feathers. Sleeping on the bed with him is his smart",
            "phone, VolaTek Star Model IX, alarming to break his sweet dreams."
        )
        "Jack".speak(
            "Okay, then. Today is May 15th, another workday. I shall check my task list again",
            "before heading my way to the Royal Library. Let me see... I'm confident that I left",
            "it on my desk yesterday."
        )
        pPrelude.finish()
    }

    val aFindTaskList = action(
        name = "Find your task list out",
        type = ActionEntryType.Context,
        description = "You shall left your task list on your desk last night... Check it out " +
                "before going to work.",
        condition = pFindTaskList.requireActive
    ) {
        narrate(
            "The desk is a mess. Piles of documents stack on each other in a fashion of pure",
            "chaos and scraps of paper is everywhere, making the most simple things like looking",
            "for a piece of paper as hard as saving the world. Okay after all, but could he even",
            "pick it out without doing some sorting work?"
        )
        narrate(
            "Maybe it's not that bad after all? The paper just, magically, showed itself up on",
            "the center of the desk. Or, less magically, just sat there overnight. The latter is",
            "more reasonable since he could just handled it as the last thing at the evening,",
            "right? Whatever. The most important thing is, he finally found his task list."
        )
        narrate(
            "Let's have a peek on this. It says that he should find evidence that the most recent",
            "discovery of \"E.F.\", The \"information transceiver\" is already existent several",
            "hundred years before the foundation of kingdom. That's the sole thing to do today,",
            "which seems to be somewhat more difficult than tasks he tackled in the past."
        )
        narrate(
            "As his home is no more than half a kilometer from the library, he just walked to it",
            "instead of relying on some sort of public transportation, like the brand-new",
            "installation of mag-lev city trains."
        )
        "Jack".speak(
            "Let me think about it."
        )
        pFindTaskList.finish()
    }
}
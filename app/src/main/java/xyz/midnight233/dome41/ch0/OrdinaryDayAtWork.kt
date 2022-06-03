package xyz.midnight233.dome41.ch0

import xyz.midnight233.littera.content.Segment

object OrdinaryDayAtWork : Segment() {
    val pPrelude by plot

    init {

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
            "Today is another workday"
        )
    }
}
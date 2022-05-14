package xyz.midnight233.littera.stateful

class JournalEntry(
    val type: JournalEntryType,
    val contents: List<String>
) {
    companion object {
        val default: JournalEntry get() = JournalEntry(JournalEntryType.Narration, listOf(""))
    }
}

enum class JournalEntryType {
    Narration, Dialogue, Notification, Prompt, Answer
}